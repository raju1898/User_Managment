package com.SpringSecurtiyOwn.SpringSecurtiyOwnModel;

public class LoginResponse {
	
	private String username;
	private String passowrd;
	private String token;
	
	
	public LoginResponse() {
		super();
	}


	

	public LoginResponse(String username, String passowrd, String token) {
		super();
		this.username = username;
		this.passowrd = passowrd;
		this.token = token;
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




	public String getToken() {
		return token;
	}




	public void setToken(String token) {
		this.token = token;
	}
	
	
	

}
