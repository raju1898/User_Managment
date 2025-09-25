package com.SpringSecurtiyOwn.SpringSecurtiyOwnModel;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles")
public class Role {
	
	private Long Id;
	private String name;
	
	public Role() {
		super();
	}

	public Role(Long id, String name) {
		super();
		Id = id;
		this.name = name;
	}

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    
}
