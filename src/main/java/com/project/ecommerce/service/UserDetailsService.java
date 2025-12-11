package com.project.ecommerce.service;

import com.project.ecommerce.dto.UpdateUserDetailsDto;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.entity.UserDetails;
import com.project.ecommerce.exception.GenericException;
import com.project.ecommerce.repository.UserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsService {

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    public Optional<UserDetails> findByEmail(String email){
        return userDetailsRepository.findByEmail(email);
    }

    public UserDetails save(UserDetails userDetails){
        return userDetailsRepository.save(userDetails);
    }

    public UserDetails findUserDetailsByUser(User user){
        return userDetailsRepository.findByUser(user).orElse(null);
    }

    public void updateUserDetails(UpdateUserDetailsDto updateUserDetailsDto, User user) throws GenericException{
        UserDetails userDetails = userDetailsRepository.findByUser(user).orElseThrow(() -> new GenericException("User not found"));
        userDetails.setTitle(updateUserDetailsDto.getTitle());
        userDetails.setFirstName(updateUserDetailsDto.getFirstName());
        userDetails.setLastName(updateUserDetailsDto.getLastName());
        userDetails.setPhoneNumber(updateUserDetailsDto.getPhoneNumber());
        userDetails.setAddressLine1(updateUserDetailsDto.getAddressLine1());
        userDetails.setAddressLine2(updateUserDetailsDto.getAddressLine2());
        userDetails.setAddressLine3(updateUserDetailsDto.getAddressLine3());
        userDetails.setPincode(updateUserDetailsDto.getPincode());
        userDetailsRepository.save(userDetails);
    }
}
