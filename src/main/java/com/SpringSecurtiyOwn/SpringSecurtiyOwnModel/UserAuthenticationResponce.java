package com.SpringSecurtiyOwn.SpringSecurtiyOwnModel;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="UserAuthenticationResponce")
public class UserAuthenticationResponce {
	
	 @Id
	  @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String token;
	public UserAuthenticationResponce() {
		super();
	}
	public UserAuthenticationResponce(String token) {
		super();
		this.token = token;
	}
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
