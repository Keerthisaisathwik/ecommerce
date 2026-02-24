package com.project.ecommerce.service;

import com.project.ecommerce.entity.User;
import com.project.ecommerce.repository.UserRepository;
import com.project.ecommerce.security.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtHelper jwtHelper;

    public User save(User user){
        return userRepository.save(user);
    }

    public User findUserByToken(String token){
        String username = jwtHelper.getUsernameByToken(token);
        return userRepository.findByUsername(username).orElse(null);
    }

    public boolean validateJwtToken(String token){
        return jwtHelper.validateToken(token);
    }
}
