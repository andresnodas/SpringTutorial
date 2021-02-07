package com.andresnodas.tutorial.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.andresnodas.tutorial.dto.UserDto;
import com.andresnodas.tutorial.entity.UserEntity;
import com.andresnodas.tutorial.repository.UserRepository;
import com.andresnodas.tutorial.service.UserService;
import com.andresnodas.tutorial.utils.RandomUtils;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RandomUtils randomUtils;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Override
	public UserDto createUser(UserDto userDto) {
		
		if(userRepository.findByEmail(userDto.getEmail()) != null)
			throw new RuntimeException("Record already exists!");
		
		userDto.getAddresses().forEach(address -> {
			address.setUserDetails(userDto);
			address.setAddressId(randomUtils.generateAddressId(30));
		});
		
		ModelMapper modelMapper = new ModelMapper();
		
		UserEntity userEntity = new UserEntity();
		
//		BeanUtils.copyProperties(userDto, userEntity);
		userEntity = modelMapper.map(userDto, UserEntity.class);
		
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
		userEntity.setUserId(randomUtils.generateUserId(30));
		
		UserEntity storedUserDetails = userRepository.save(userEntity);
		
		UserDto returnValue = new UserDto();
//		BeanUtils.copyProperties(storedUserDetails, returnValue);
		returnValue = modelMapper.map(storedUserDetails, UserDto.class);
		
		return returnValue;
	}
	
	@Override
	public UserDto getUser(String email) {
		
		UserEntity userEntity = userRepository.findByEmail(email);
		
		if(userEntity == null) 
			throw new UsernameNotFoundException(email);
		
		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(userEntity, returnValue);
		
		return returnValue;
	}

	@Override
	public List<UserDto> getUsers(int page, int limit) {
		
		List<UserDto> returnValue = new ArrayList<>();
		
		Pageable pageableRequest = PageRequest.of(page, limit);
		
		Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);
		List<UserEntity> users = usersPage.getContent();
		
		returnValue.addAll(users.stream().map(user -> {
			UserDto userDto = new UserDto();
			
			BeanUtils.copyProperties(user, userDto);
			
			return userDto;
		}).collect(Collectors.toList()));
		
		return returnValue;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		UserEntity userEntity = userRepository.findByEmail(email);
		
		if(userEntity == null) 
			throw new UsernameNotFoundException(email);
		
		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
	}

	@Override
	public UserDto getUserByUserId(String userId) {
		
		UserDto returnValue = new UserDto();
		UserEntity userEntity = userRepository.findByUserId(userId);
		
		if(userEntity == null)
			throw new UsernameNotFoundException(userId);
		
		BeanUtils.copyProperties(userEntity, returnValue);
		
		return returnValue;
	}

	@Override
	public UserDto updateUser(UserDto userDto) {
		
		UserEntity userEntity = userRepository.findByUserId(userDto.getUserId());
		
		if(userEntity == null)
			throw new UsernameNotFoundException(userDto.getUserId());
		
		userEntity.setFirstName(userDto.getFirstName());
		userEntity.setLastName(userDto.getLastName());
		
		UserEntity updatedUserEntity = userRepository.save(userEntity);
		
		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(updatedUserEntity, returnValue);
		
		return returnValue;
	}

	@Override
	public void deleteUser(String userId) {
		
		UserEntity userEntity = userRepository.findByUserId(userId);
		
		if(userEntity == null)
			throw new UsernameNotFoundException(userId);

		userRepository.delete(userEntity);
		
	}

}
