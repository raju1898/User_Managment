package com.SpringSecurtiyOwn.SpringSecurtiyOwnController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

import com.SpringSecurtiyOwn.SpringSecurtiyOwnJwtHelper.JwtHelper;
import com.SpringSecurtiyOwn.SpringSecurtiyOwnModel.LoginResponse;
import com.SpringSecurtiyOwn.SpringSecurtiyOwnModel.RefreshToken;
import com.SpringSecurtiyOwn.SpringSecurtiyOwnModel.SignupRequest;
import com.SpringSecurtiyOwn.SpringSecurtiyOwnModel.TokenRefreshResponse;
import com.SpringSecurtiyOwn.SpringSecurtiyOwnModel.User;
import com.SpringSecurtiyOwn.SpringSecurtiyOwnModel.UserAuthenticatioinR;
import com.SpringSecurtiyOwn.SpringSecurtiyOwnModel.UserDetailsImpl;
import com.SpringSecurtiyOwn.SpringSecurtiyOwnService.CustomUserDetailsService;
import com.SpringSecurtiyOwn.SpringSecurtiyOwnService.RefreshTokenService;

@RestController
@CrossOrigin(origins = "http://localhost:3000", methods = { RequestMethod.GET, RequestMethod.POST })
@RequestMapping("/auth")

public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	private RefreshTokenService refreshTokenService;

	@Autowired
	private JwtHelper jwtHelper;

	public AuthController(CustomUserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody UserAuthenticatioinR userAuthenticatioinR) {
		try {
			Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					userAuthenticatioinR.getUserName(), userAuthenticatioinR.getPassword()));

			if (auth.isAuthenticated()) {
				userDetailsService.resetFailedAttempts(userAuthenticatioinR.getUserName());
				String token = jwtHelper.generateToken(userAuthenticatioinR.getUserName());
				return ResponseEntity.ok(new LoginResponse(userAuthenticatioinR.getUserName(), null, token));
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
			}

		} catch (BadCredentialsException e) {
			userDetailsService.increaseFailedAttempts(userAuthenticatioinR.getUserName());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());

		} catch (LockedException e) {
			System.out.println(e.getMessage() + "getMessagegetMessage");
			return ResponseEntity.status(HttpStatus.LOCKED).body(e.getMessage());

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@PostMapping(value = "/signup")
	public ResponseEntity<Void> signUp(@RequestBody SignupRequest signupRequest) {

		userDetailsService.addUser(signupRequest);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PostMapping(value = "/refresh")
	public ResponseEntity<?> refreshToken(
			@CookieValue(name = "refreshToken", required = false) String requestRefreshToken) {
		if (requestRefreshToken == null)
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("no refresh token");

		return refreshTokenService.findByToken(requestRefreshToken).map(refreshTokenService::verifyExpiration)
				.map(RefreshToken::getUser).map(user -> {
					String token = jwtHelper.generateAccessToken(UserDetailsImpl.build(user));
					String newRefreshToken = refreshTokenService.createRefreshToken(user.getId()).getToken();
					return ResponseEntity.ok(new TokenRefreshResponse(token, newRefreshToken));
				}).orElseThrow(() -> new RuntimeException("Refresh token not found"));

	}

}
