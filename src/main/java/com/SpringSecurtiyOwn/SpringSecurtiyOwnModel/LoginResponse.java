package com.SpringSecurtiyOwn.SpringSecurtiyOwnModel;

public class LoginResponse {
	
	private String username;
	private String passowrd;
	
	
	public LoginResponse() {
		super();
	}


	public LoginResponse(String username, String passowrd) {
		super();
		this.username = username;
		this.passowrd = passowrd;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassowrd() {
		return passowrd;
	}


	public void setPassowrd(String passowrd) {
		this.passowrd = passowrd;
	}
	
	
	

}
