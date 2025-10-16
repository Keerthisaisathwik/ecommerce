package com.project.ecommerce.service;

import com.project.ecommerce.entity.UserDetails;
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
}
