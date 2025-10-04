package com.SpringSecurtiyOwn.SpringSecurtiyOwnService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.SpringSecurtiyOwn.SpringSecurtiyOwnModel.Role;
import com.SpringSecurtiyOwn.SpringSecurtiyOwnModel.SignupRequest;
import com.SpringSecurtiyOwn.SpringSecurtiyOwnModel.User;
import com.SpringSecurtiyOwn.SpringSecurtiyOwnModel.LoginAttempt;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private JdbcTemplate jdbcTemplate;

	private PasswordEncoder passwordEncoder;
	private static final int MAX_FAILED_ATTEMPTS = 3;
	private static final int LOCK_TIME_DURATION = 30;

	@Autowired
	public CustomUserDetailsService(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
		this.jdbcTemplate = jdbcTemplate;
		this.passwordEncoder = passwordEncoder;

	}

	public User addUser(SignupRequest signupRequest) {
		User user = new User();
		user.setUsername(signupRequest.getUserName());
		user.setPassword(signupRequest.getPassword());

		Role role = new Role();
		role.setName(signupRequest.getRole().toUpperCase()); // e.g. "user" → "ROLE_USER"
		user.setRoles(Set.of(role));

		// Check if user exists
		String findQuery = "SELECT COUNT(*) FROM users WHERE username = ?";
		Integer count = jdbcTemplate.queryForObject(findQuery, Integer.class, user.getUsername());
		if (count != null && count > 0) {
			throw new RuntimeException("User already exists!");
		}

		try {
			String hashPassword = passwordEncoder.encode(user.getPassword());
			String insertUserSql = "INSERT INTO users(username, password, role,enabled) VALUES (?, ?, ?,?)";
			jdbcTemplate.update(insertUserSql, user.getUsername(), hashPassword, role.getName(), true);
			String userIdQuery = "SELECT id FROM users WHERE username = ?";
			Long userId = jdbcTemplate.queryForObject(userIdQuery, Long.class, user.getUsername());
			String roleName = role.getName();
			String roleIdQuery = "SELECT id FROM roles WHERE name = ?";
			Long roleId;
			try {
				roleId = jdbcTemplate.queryForObject(roleIdQuery, Long.class, roleName);
			} catch (EmptyResultDataAccessException e) {
				jdbcTemplate.update("INSERT INTO roles(name) VALUES(?)", roleName);
				roleId = jdbcTemplate.queryForObject(roleIdQuery, Long.class, roleName);
			}
			String userRoleSql = "INSERT INTO user_roles(user_id, role_id) VALUES (?, ?)";
			jdbcTemplate.update(userRoleSql, userId, roleId);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return user;
	}

	private boolean isDataExistByQuery(String query) {
		boolean result = true;

		try {
			int count = jdbcTemplate.queryForObject(query, Integer.class);
			if (count == 0)
				result = false;
		} catch (Exception e) {

		}

		return result;

	}

	@SuppressWarnings("deprecation")
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			String QUERY = "SELECT username, password, failed_attempts, lock_time FROM users WHERE username = ?";

			User user = jdbcTemplate.queryForObject(QUERY, new Object[] { username }, (rs, rowNum) -> {
				User u = new User();
				u.setUsername(rs.getString("username"));
				u.setPassword(rs.getString("password"));
				u.setFailedAttempts(rs.getInt("failed_attempts"));
				Timestamp lockTime = rs.getTimestamp("lock_time");
				if (lockTime != null) {
					u.setLockTime(lockTime.toLocalDateTime());
				}
				return u;
			});

			// ✅ Check lock
			if (user.getLockTime() != null) {
				LocalDateTime unlockTime = user.getLockTime().plusMinutes(LOCK_TIME_DURATION);
				if (LocalDateTime.now().isBefore(unlockTime)) {
					throw new LockedException("Account is locked. Try again after 30 minutes.");
				} else {
					resetFailedAttempts(user.getUsername()); // auto-unlock
				}
			}

			return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
					.password(user.getPassword()).roles("USER").accountLocked(false).build();

		} catch (EmptyResultDataAccessException e) {
			// ✅ handle fake usernames too
			increaseFailedAttemptsForFakeUsername(username);
			throw new UsernameNotFoundException("Invalid username or password");
		}
	}

	// ================== LOGIN ATTEMPT HANDLING ==================

	public void increaseFailedAttempts(String username) {
		String getIdSql = "SELECT id FROM users WHERE username = ?";
		Long userId = jdbcTemplate.queryForObject(getIdSql, Long.class, username);

		String updateSql = "UPDATE users SET failed_attempts = failed_attempts + 1 WHERE id = ?";
		jdbcTemplate.update(updateSql, userId);

		String selectSql = "SELECT failed_attempts FROM users WHERE id = ?";
		Integer attempts = jdbcTemplate.queryForObject(selectSql, Integer.class, userId);

		if (attempts >= MAX_FAILED_ATTEMPTS) {
			lockAccount(userId);
		}
	}

	public void resetFailedAttempts(String username) {
		String getIdSql = "SELECT id FROM users WHERE username = ?";
		Long userId = jdbcTemplate.queryForObject(getIdSql, Long.class, username);

		String updateSql = "UPDATE users SET failed_attempts = 0, lock_time = NULL WHERE id = ?";
		jdbcTemplate.update(updateSql, userId);
	}

	public void lockAccount(Long userId) {
		String sql = "UPDATE users SET lock_time = ? WHERE id = ?";
		jdbcTemplate.update(sql, LocalDateTime.now(), userId);
	}

	// ✅ fake username attempts
	public void increaseFailedAttemptsForFakeUsername(String username) {
		String sql = "SELECT * FROM login_attempts WHERE attempted_username = ?";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, username);

		if (rows.isEmpty()) {
			String insertSql = "INSERT INTO login_attempts (attempted_username, attempts, lock_time) VALUES (?, 1, NULL)";
			jdbcTemplate.update(insertSql, username);
		} else {
			int attempts = (int) rows.get(0).get("attempts");
			Object lockTimeObj = rows.get(0).get("lock_time");
			LocalDateTime lockTime = null;

			if (lockTimeObj instanceof Timestamp) {
				lockTime = ((Timestamp) lockTimeObj).toLocalDateTime();
			}

			if (lockTime != null) {
				LocalDateTime unlockTime = lockTime.plusMinutes(LOCK_TIME_DURATION);
				if (LocalDateTime.now().isBefore(unlockTime)) {
					throw new LockedException("Account is locked. Try again after 30 minutes.");
				} else {
					resetFailedAttemptsForFakeUsername(username);
					attempts = 0;
				}
			}

			attempts++;
			if (attempts >= MAX_FAILED_ATTEMPTS) {
				String updateSql = "UPDATE login_attempts SET attempts = ?, lock_time = NOW() WHERE attempted_username = ?";
				jdbcTemplate.update(updateSql, attempts, username);
				throw new LockedException("Account is locked. Try again after 30 minutes.");
			} else {
				String updateSql = "UPDATE login_attempts SET attempts = ? WHERE attempted_username = ?";
				jdbcTemplate.update(updateSql, attempts, username);
			}
		}
	}

	public void resetFailedAttemptsForFakeUsername(String username) {
		String sql = "UPDATE login_attempts SET attempts = 0, lock_time = NULL WHERE attempted_username = ?";
		jdbcTemplate.update(sql, username);
	}
}
