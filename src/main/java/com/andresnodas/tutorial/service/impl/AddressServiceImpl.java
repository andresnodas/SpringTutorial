package com.andresnodas.tutorial.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.andresnodas.tutorial.dto.AddressDto;
import com.andresnodas.tutorial.entity.AddressEntity;
import com.andresnodas.tutorial.entity.UserEntity;
import com.andresnodas.tutorial.repository.AddressRepository;
import com.andresnodas.tutorial.repository.UserRepository;
import com.andresnodas.tutorial.service.AddressService;
import com.andresnodas.tutorial.utils.ErrorMessages;

@Service
public class AddressServiceImpl implements AddressService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AddressRepository addressRepository;
	
	@Override
	public List<AddressDto> getAddresses(String userId) {
		
		List<AddressDto> returnValue = new ArrayList<>();
		ModelMapper modelMapper = new ModelMapper();
		
		UserEntity userEntity = userRepository.findByUserId(userId);
		if(userEntity == null)
			return returnValue;
		
		Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);
		
		for(AddressEntity address : addresses)
			returnValue.add(modelMapper.map(address, AddressDto.class));
		
		return returnValue;
	}

	@Override
	public AddressDto getAddress(String addressId) {
		
		AddressEntity addressEntity = addressRepository.findByAddressId(addressId);
		
		if(addressEntity == null)
			throw new RuntimeException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		
		ModelMapper modelMapper = new ModelMapper();
		
		return modelMapper.map(addressEntity, AddressDto.class);
	}

}
