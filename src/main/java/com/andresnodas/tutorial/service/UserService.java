package com.andresnodas.tutorial.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.andresnodas.tutorial.dto.UserDto;

public interface UserService extends UserDetailsService {
	
	public UserDto createUser(UserDto userDto);
	public UserDto getUser(String email);
	public List<UserDto> getUsers(int page, int limit);
	public UserDto getUserByUserId(String userId);
	public UserDto updateUser(UserDto userDto);
	public void deleteUser(String userId);
	
}
