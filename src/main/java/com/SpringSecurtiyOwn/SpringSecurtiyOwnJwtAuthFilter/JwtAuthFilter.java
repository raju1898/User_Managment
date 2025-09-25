package com.SpringSecurtiyOwn.SpringSecurtiyOwnJwtAuthFilter;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.SpringSecurtiyOwn.SpringSecurtiyOwnJwtHelper.JwtHelper;
import com.SpringSecurtiyOwn.SpringSecurtiyOwnService.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JwtAuthFilter extends OncePerRequestFilter {

	private final CustomUserDetailsService customeUserDetailsService;
	private final ObjectMapper objectMapper;

	public JwtAuthFilter(CustomUserDetailsService customeUserDetailsService, ObjectMapper objectMapper) {

		this.customeUserDetailsService = customeUserDetailsService;
		this.objectMapper = objectMapper;

	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		try {
			response.setHeader("Access-control-Allow-Origin", "*");
			response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
			response.setHeader("Access-Control-Allow-Headers", "x-requested-with, x-auth-token");
			response.setHeader("Access-Control-Max-Age", "3600");
			response.setHeader("Access-Control-Allow-Credentials", "true");

			String authHeader = request.getHeader("Authroziation");
			String username = null;
			String token = null;
			if (authHeader != null && authHeader.startsWith("bearer")) {
				token = authHeader.substring(7);
				username = JwtHelper.extractUsername(token);
				if (token == null) {
					filterChain.doFilter(request, response);

					return;
				}
			}
		} catch (AccessDeniedException e) {

		}

	}

}
