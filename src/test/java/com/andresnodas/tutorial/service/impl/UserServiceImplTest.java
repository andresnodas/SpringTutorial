package com.andresnodas.tutorial.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.andresnodas.tutorial.dto.UserDto;
import com.andresnodas.tutorial.entity.UserEntity;
import com.andresnodas.tutorial.repository.UserRepository;

class UserServiceImplTest {

	@InjectMocks
	private UserServiceImpl userService;
	
	@Mock
	private UserRepository userRepository;
	
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testGetUser() {
		
		UserEntity userEntity = new UserEntity();
		userEntity.setId(1L);
		userEntity.setFirstName("Andres");
		userEntity.setUserId("askdlajsdlkas");
		userEntity.setEncryptedPassword("ashkdakljsdajskld");
		
		when( userRepository.findByEmail(anyString()) ).thenReturn(userEntity);
		
		UserDto userDto = userService.getUser("test@test.com");
		
		assertNotNull(userDto);
		assertEquals("Andres", userDto.getFirstName());
	}

	@Test
	void testGetUser_UsernameNotFoundException() {
		when( userRepository.findByEmail(anyString()) ).thenReturn(null);
		
		assertThrows(UsernameNotFoundException.class, 
				() -> {
					userService.getUser("test@test.com");
				}
			);
	}
	
}
