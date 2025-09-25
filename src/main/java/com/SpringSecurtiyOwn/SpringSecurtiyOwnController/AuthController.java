package com.SpringSecurtiyOwn.SpringSecurtiyOwnController;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SpringSecurtiyOwn.SpringSecurtiyOwnJwtHelper.JwtHelper;
import com.SpringSecurtiyOwn.SpringSecurtiyOwnModel.LoginResponse;
import com.SpringSecurtiyOwn.SpringSecurtiyOwnModel.UserAuthenticatioinR;
import com.SpringSecurtiyOwn.SpringSecurtiyOwnService.CustomUserDetailsService;
import com.SpringSecurtiyOwn.SpringSecurtiyOwnModel.User;


@RestController
@RequestMapping("/auth")
public class AuthController {
	
	 @Autowired  private AuthenticationManager authenticationManager;
	
	  @Autowired 
	  private CustomUserDetailsService userDetailsService;
	  
	  public AuthController(CustomUserDetailsService userDetailsService) {
		  this.userDetailsService=userDetailsService;
		  
	  }
	  
	  
	  @PostMapping(value = "/login")
	  public ResponseEntity<LoginResponse> login(@RequestBody UserAuthenticatioinR  userAuthenticatioinR){
		  
		  System.out.println("method called");
		
		
		  org.springframework.security.core.Authentication auth=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userAuthenticatioinR.getUsername(), userAuthenticatioinR.getPassword()));
		  
		  if(auth.isAuthenticated()) {
		
		String token=JwtHelper.generateToken(userAuthenticatioinR.getUsername());
		UserDetails user=userDetailsService.loadUserByUsername(userAuthenticatioinR.getUsername());
		System.out.println("userrrrrrrrr"+user);
		 return ResponseEntity.ok(new LoginResponse(token,userAuthenticatioinR.getUsername()));
		
		  }
		  return null;
		 
		 	
	}
	  @PostMapping(value = "/signup")
	  public ResponseEntity<Void> signUp(@RequestBody User user) {

	      System.out.println("method called============="+user);
	      
	      userDetailsService.addUser(user);

	     
	      return ResponseEntity.status(HttpStatus.CREATED).build();
	  }


}
