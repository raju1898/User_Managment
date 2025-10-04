package com.SpringSecurtiyOwn.SpringSecurtiyOwnModel;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="UserAuthenticatioinR")
public class UserAuthenticatioinR {
	
	 @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String  userName;
	private  String password;
	
	public UserAuthenticatioinR() {
		super();
	}

	public UserAuthenticatioinR(String userName, String password) {
		super();
		this.userName = userName;
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUsername(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	

}
