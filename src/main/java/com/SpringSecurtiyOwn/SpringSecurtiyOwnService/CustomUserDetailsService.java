package com.SpringSecurtiyOwn.SpringSecurtiyOwnService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.SpringSecurtiyOwn.SpringSecurtiyOwnModel.User;


@Service
public class CustomUserDetailsService implements UserDetailsService {
	
	
	private JdbcTemplate  jdbcTemplate;
	

	private PasswordEncoder passwordEncoder;
	
	@Autowired
	public CustomUserDetailsService(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
		this.jdbcTemplate=jdbcTemplate;
		this.passwordEncoder=passwordEncoder;
		
	}
	

	@SuppressWarnings("deprecation")
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		String QUERY =  "SELECT username, password, role FROM users WHERE username = ?";		  
	return  jdbcTemplate.queryForObject(QUERY, new Object[]{username}, (rs, rowNum) -> org.springframework.security.core.userdetails.User
            .withUsername(rs.getString("username"))
            .password(rs.getString("password")) // should already be encrypted
            .roles(rs.getString("role"))        // e.g. ROLE_USER
            .build());
	}
	
	public User addUser(User user) {
	    String email = user.getUsername();
	    String findQuery="Select count(*) FROM users where username='"+email+"'";
	    boolean isExist = isDataExistByQuery(findQuery);
	    if(isExist) {
	    	System.out.println("DATA ALREADY EXISTED");
	    }
	    
	    try {
			String hashPassword =passwordEncoder.encode(user.getPassword());
			String insertQuery="insert into users(username, password, role)value(?,?,?)";
		    jdbcTemplate.update(insertQuery,user.getUsername(),hashPassword,user.getRole());
		    
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return user;
	}
	private boolean isDataExistByQuery(String query)
	{
		boolean result = true;
		
		try {
			int count = jdbcTemplate.queryForObject(query, Integer.class);
			if(count==0)
				result = false;
		} catch (Exception e) {
			
		}
		
		return result;

	}

}


	