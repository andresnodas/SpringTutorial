package com.andresnodas.tutorial.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.andresnodas.tutorial.dto.UserDto;

public interface UserService extends UserDetailsService {
	
	public UserDto createUser(UserDto userDto);
	public UserDto getUser(String email);
	public UserDto getUserByUserId(String userId);
	public UserDto updateUser(UserDto userDto);
	
}
