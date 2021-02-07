package com.andresnodas.tutorial.security;

import com.andresnodas.tutorial.SpringAppContext;

public class SecurityConstants {

	public static final long EXPIRATION_TIME = 864000000; //in milisecond 10days
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_STRING = "Authorization";
	public static final String SIGNUP_URL = "/users";
	
	public static String getTokenSecret() {
		AppProperties appProperties = (AppProperties) SpringAppContext.getBean("appProperties");
		
		return appProperties.getTokenSecret();
	}
	
}
