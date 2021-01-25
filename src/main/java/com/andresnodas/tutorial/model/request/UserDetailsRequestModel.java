package com.andresnodas.tutorial.model.request;

import java.util.List;

import com.zaxxer.hikari.util.FastList;

public class UserDetailsRequestModel {

	private String firstName;
	private String lastName;
	private String email;
	private String password;
	
	private List<UserDetailsRequestModelItem> items = new FastList<>(UserDetailsRequestModelItem.class);

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<UserDetailsRequestModelItem> getItems() {
		return items;
	}

	public void setItems(List<UserDetailsRequestModelItem> items) {
		this.items = items;
	}

}
