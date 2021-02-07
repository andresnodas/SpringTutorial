package com.andresnodas.tutorial.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.andresnodas.tutorial.dto.AddressDto;
import com.andresnodas.tutorial.dto.UserDto;
import com.andresnodas.tutorial.exceptions.UserServiceException;
import com.andresnodas.tutorial.model.request.UserDetailsRequestModel;
import com.andresnodas.tutorial.model.response.AddressRest;
import com.andresnodas.tutorial.model.response.OperationStatusModel;
import com.andresnodas.tutorial.model.response.RequestOperationName;
import com.andresnodas.tutorial.model.response.RequestOperationStatus;
import com.andresnodas.tutorial.model.response.UserRest;
import com.andresnodas.tutorial.service.AddressService;
import com.andresnodas.tutorial.service.UserService;
import com.andresnodas.tutorial.utils.ErrorMessages;

@RestController
@RequestMapping("/users") //http://localhost:8080/users
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private AddressService addressService;
	
	@GetMapping(path="/{id}",
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public UserRest getUser(@PathVariable String id)
	{
		UserRest returnValue = new UserRest();
		
		UserDto userDto = userService.getUserByUserId(id);
		BeanUtils.copyProperties(userDto, returnValue);
		
		return returnValue;
	}
	
	@GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "limit", defaultValue = "25") int limit)
	{
		
		List<UserRest> returnValue = new ArrayList<>();
		
		List<UserDto> users = userService.getUsers(page, limit);
		
		returnValue.addAll(users.stream().map(user -> {
			UserRest userRest = new UserRest();
			
			BeanUtils.copyProperties(user, userRest);
			
			return userRest;
		}).collect(Collectors.toList()));
		
		return returnValue;
	}
	
	@PostMapping(
			consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws UserServiceException
	{
//		Assert.hasLength(userDetails.getFirstName(), ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
		
		if(userDetails.getFirstName().isEmpty()) throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
		
		UserRest returnValue = new UserRest();
		
//		UserDto userDto = new UserDto();
//		BeanUtils.copyProperties(userDetails, userDto);
		
		ModelMapper modelMapper = new ModelMapper();
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);
		
		UserDto createdUser = userService.createUser(userDto);
//		BeanUtils.copyProperties(createdUser, returnValue);
		returnValue = modelMapper.map(createdUser, UserRest.class);
		
		return returnValue;
	}
	
	@PutMapping(path="/{id}",
			consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails)
	{
		UserRest returnValue = new UserRest();
		
		UserDto userDto = new UserDto();
		userDto.setUserId(id);
		BeanUtils.copyProperties(userDetails, userDto);
		
		UserDto updatedUser = userService.updateUser(userDto);
		BeanUtils.copyProperties(updatedUser, returnValue);
		
		return returnValue;
	}
	
	@DeleteMapping(path="/{id}",
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public OperationStatusModel deleteUser(@PathVariable String id)
	{
		OperationStatusModel returnValue = new OperationStatusModel();
		
		userService.deleteUser(id);
		
		returnValue.setOperationName(RequestOperationName.DELETE.name());
		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		
		return returnValue;
	}
	
	@GetMapping(path="/{id}/addresses",
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public CollectionModel<AddressRest> getUserAddresses(@PathVariable String id)
	{
		List<AddressRest> returnValue = new ArrayList<>();
		
		List<AddressDto> addressesDto = addressService.getAddresses(id);
		
		if(addressesDto != null && !addressesDto.isEmpty()) {
			
			Type listType = new TypeToken<List<AddressRest>>() {}.getType();
			ModelMapper modelMapper = new ModelMapper();
			returnValue = modelMapper.map(addressesDto, listType);
			
			returnValue.forEach(addressRest -> {
				Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddress(id, addressRest.getAddressId())).withSelfRel();
				
				addressRest.add(selfLink);
			});
			
		}
		
		Link userLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUser(id)).withRel("user");
		Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(id)).withSelfRel();
		
		return CollectionModel.of(returnValue, userLink, selfLink);
	}
	
	@GetMapping(path="/{userId}/addresses/{addressId}",
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public EntityModel<AddressRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId)
	{
		AddressDto addressDto = addressService.getAddress(addressId);
		
		ModelMapper modelMapper = new ModelMapper();
		AddressRest returnValue = modelMapper.map(addressDto, AddressRest.class);
		
		Link userLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUser(userId)).withRel("user");
		Link userAddressesLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");
		Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddress(userId, addressId)).withSelfRel();

		return EntityModel.of(returnValue, userLink, userAddressesLink, selfLink);
	}
}
