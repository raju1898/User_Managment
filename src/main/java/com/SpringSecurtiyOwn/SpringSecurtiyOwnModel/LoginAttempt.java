package com.SpringSecurtiyOwn.SpringSecurtiyOwnModel;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "login_attempts")
public class LoginAttempt {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "attempted_username", nullable = false)
	private String attemptedUsername;

	@Column(name = "attempts", nullable = false)
	private int attempts = 0;

	@Column(name = "lock_time")
	private LocalDateTime lockTime;

	public LoginAttempt() {
		super();
	}

	public LoginAttempt(Long id, String attemptedUsername, int attempts, LocalDateTime lockTime) {
		super();
		this.id = id;
		this.attemptedUsername = attemptedUsername;
		this.attempts = attempts;
		this.lockTime = lockTime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAttemptedUsername() {
		return attemptedUsername;
	}

	public void setAttemptedUsername(String attemptedUsername) {
		this.attemptedUsername = attemptedUsername;
	}

	public int getAttempts() {
		return attempts;
	}

	public void setAttempts(int attempts) {
		this.attempts = attempts;
	}

	public LocalDateTime getLockTime() {
		return lockTime;
	}

	public void setLockTime(LocalDateTime lockTime) {
		this.lockTime = lockTime;
	}

}
