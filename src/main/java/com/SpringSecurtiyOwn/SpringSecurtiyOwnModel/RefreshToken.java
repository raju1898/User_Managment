package com.SpringSecurtiyOwn.SpringSecurtiyOwnModel;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.SpringSecurtiyOwn.SpringSecurtiyOwnModel.User;

@Entity
@Table(name = "/refresh_tokens")
public class RefreshToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, unique = true)
	private String token;
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	private Instant expirydate;
	private boolean revoked = false;

	public RefreshToken() {
		super();
	}

	public RefreshToken(Long id, String token, User user, Instant expirydate, boolean revoked) {
		super();
		this.id = id;
		this.token = token;
		this.user = user;
		this.expirydate = expirydate;
		this.revoked = revoked;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Instant getExpirydate() {
		return expirydate;
	}

	public void setExpirydate(Instant expirydate) {
		this.expirydate = expirydate;
	}

	public boolean isRevoked() {
		return revoked;
	}

	public void setRevoked(boolean revoked) {
		this.revoked = revoked;
	}

}
