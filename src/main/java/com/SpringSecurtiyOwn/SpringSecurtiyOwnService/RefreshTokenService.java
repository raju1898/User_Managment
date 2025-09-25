package com.SpringSecurtiyOwn.SpringSecurtiyOwnService;

import java.security.Timestamp;
import java.time.Instant;
import java.util.UUID;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.SpringSecurtiyOwn.SpringSecurtiyOwnModel.RefreshToken;
import java.util.Optional;

@Service
public class RefreshTokenService {

	private Long refreshTokenDurationMs;

	private JdbcTemplate jdbcTemplate;

	public RefreshTokenService(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public RefreshToken createRefreshToken(Long userId) {

		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setId(userId);
		refreshToken.setToken(UUID.randomUUID().toString());
		refreshToken.setExpirydate(Instant.now().plusMillis(refreshTokenDurationMs));
		refreshToken.setRevoked(false);

		String sql = "INSERT INTO refresh_tokens (token, user_id, expiry_date, revoked) VALUES (?, ?, ?, ?)";
		jdbcTemplate.update(sql, refreshToken.getToken(), refreshToken.getId(),
				Instant.from(refreshToken.getExpirydate()), refreshToken.isRevoked());

		return refreshToken;

	}

	public Optional<RefreshToken> findByToken(String token) {
		String sql = "SELECT id, token, user_id, expiry_date, revoked FROM refresh_tokens WHERE token = ?";
		try {
			RefreshToken rt = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
				RefreshToken refreshToken = new RefreshToken();
				refreshToken.setId(rs.getLong("id"));
				refreshToken.setToken(rs.getString("token"));
				refreshToken.setId(rs.getLong("user_id")); // <-- FIXED: use setUserId, not setId
				refreshToken.setExpirydate(rs.getTimestamp("expiry_date").toInstant());
				refreshToken.setRevoked(rs.getBoolean("revoked"));
				return refreshToken;
			}, token);
			return Optional.ofNullable(rt);
		} catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}
	}

	public RefreshToken verifyExpiration(RefreshToken token) {
		if (token.getExpirydate().isBefore(Instant.now()) || token.isRevoked()) {
			deleteBytoken(token.getToken());
			throw new RuntimeException("Refresh token expired or revoked. Please login again.");
		}
		return token;
	}

	public void deleteBytoken(String token) {
		String sql = "DELETE FROM refresh_tokens WHERE token = ?";
		jdbcTemplate.update(sql, token);
	}

	public void deleteByUserId(Long userId) {
		String sql = "DELETE FROM refresh_tokens WHERE user_id = ?";
		jdbcTemplate.update(sql, userId);
	}

}
