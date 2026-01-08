package com.project.ecommerce.service.impl;

import com.project.ecommerce.dto.AddressDto;
import com.project.ecommerce.dto.SaveAddressDto;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.entity.UserAddresses;
import com.project.ecommerce.exception.GenericException;
import com.project.ecommerce.repository.UserAddressesRepository;
import com.project.ecommerce.service.UserAddressesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAddressesServiceImpl implements UserAddressesService {

    private final UserAddressesRepository userAddressesRepository;

    @Override
    public List<UserAddresses> getAllAddressesOfUser(User user) {
        return userAddressesRepository.findByUser(user);
    }

    @Override
    public UserAddresses getAddressById(Long id) {
        return userAddressesRepository.findById(id).orElse(null);
    }

    @Override
    public void addNewAddress(User user, SaveAddressDto saveAddressDto) throws GenericException {
        UserAddresses userAddress = new UserAddresses().builder()
                .user(user)
                .addressLine1(saveAddressDto.getAddressLine1())
                .addressLine2(saveAddressDto.getAddressLine2())
                .addressLine3(saveAddressDto.getAddressLine3() ==null || saveAddressDto.getAddressLine3().isEmpty() ? null : saveAddressDto.getAddressLine3())
                .pincode(saveAddressDto.getPincode())
                .build();
        userAddressesRepository.save(userAddress);
    }

    @Override
    public void updateUserAddress(User user, AddressDto addressDto) throws GenericException{
        if(getAddressById(addressDto.getId()).getUser() != user){
            throw new GenericException("You cannot access this user address");
        }
        UserAddresses userAddress = userAddressesRepository.findById(addressDto.getId()).orElseThrow(() -> new GenericException("Given id not found"));
        userAddress.setAddressLine1(addressDto.getAddressLine1());
        userAddress.setAddressLine2(addressDto.getAddressLine2());
        userAddress.setAddressLine3(addressDto.getAddressLine3());
        userAddress.setPincode(addressDto.getPincode());
        userAddressesRepository.save(userAddress);
    }

    @Override
    @Transactional
    public void deleteUserAddress(User user, Long id) throws GenericException {
        if(getAddressById(id).getUser() != user){
            throw new GenericException("You cannot access this user address");
        }
        userAddressesRepository.deleteById(id);
    }
}
