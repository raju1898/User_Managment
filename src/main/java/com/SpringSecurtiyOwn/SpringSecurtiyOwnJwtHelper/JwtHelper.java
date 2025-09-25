package com.SpringSecurtiyOwn.SpringSecurtiyOwnJwtHelper;

import java.awt.RenderingHints.Key;
import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

public class JwtHelper {
	private static final  SecretKey SECRET_KEY=  Keys.secretKeyFor(SignatureAlgorithm.HS256);
	
	private static final int Miutes=60;
	
	
	public static String generateToken(String email) {
		var now=Instant.now();
		return Jwts.builder()
				.setSubject(email)
				.setIssuedAt(Date.from(now))
				.signWith(SignatureAlgorithm.HS256, SECRET_KEY)
				.compact();
		
	}
	public static String extractUsername(String token) {
		return getTokenBody(token).getSubject();
		
	}
	public static Boolean validateToken(String token, UserDetails userDetails) {
	    final String username = extractUsername(token);
	    return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	  }
	 private static boolean isTokenExpired(String token) {
		    Claims claims = getTokenBody(token);
		    return claims.getExpiration().before(new Date());
		  }
	 
	 
	public static Claims getTokenBody(String token) {
		try {
			return Jwts
                    .parserBuilder()   
					.setSigningKey(SECRET_KEY)
					.build()
					  .parseClaimsJws(token)
			          .getBody();
		}
		catch(Exception e) {
			throw new RuntimeException("Access denied: " + e.getMessage());
			
		}
	}
	

}
