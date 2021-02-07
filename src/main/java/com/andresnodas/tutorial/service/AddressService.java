package com.andresnodas.tutorial.service;

import java.util.List;

import com.andresnodas.tutorial.dto.AddressDto;

public interface AddressService {

	List<AddressDto> getAddresses(String userId);
	AddressDto getAddress(String addressId);
	
}
