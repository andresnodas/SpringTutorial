package com.andresnodas.tutorial.exceptions;

public class UserServiceException extends RuntimeException {

	private static final long serialVersionUID = -8299286122508934656L;

	public UserServiceException(String message) {
		super(message);
	}
	
}
