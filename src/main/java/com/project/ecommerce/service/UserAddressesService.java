package com.project.ecommerce.service;

import com.project.ecommerce.dto.AddressDto;
import com.project.ecommerce.dto.SaveAddressDto;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.entity.UserAddresses;
import com.project.ecommerce.exception.GenericException;

import java.util.List;

public interface UserAddressesService {

    List<UserAddresses> getAllAddressesOfUser(User user);

    UserAddresses getAddressById(Long id);

    void addNewAddress(User user, SaveAddressDto saveAddressDto) throws GenericException;

    void updateUserAddress(User user, AddressDto addressDto) throws GenericException;

    void deleteUserAddress(User user, Long id) throws GenericException;
}
