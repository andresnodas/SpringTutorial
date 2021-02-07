package com.andresnodas.tutorial.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.andresnodas.tutorial.entity.AddressEntity;
import com.andresnodas.tutorial.entity.UserEntity;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, Long> {

	List<AddressEntity> findAllByUserDetails(UserEntity userEntity);
	AddressEntity findByAddressId(String addressId);
	
}
