package com.project.ecommerce.scheduler;

import com.project.ecommerce.entity.User;
import com.project.ecommerce.entity.UserDetails;
import com.project.ecommerce.repository.UserDetailsRepository;
import com.project.ecommerce.repository.UserRepository;
import com.project.ecommerce.service.UserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class Scheduler {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserDetailsRepository userDetailsRepository;
    @Autowired
    UserDetailsService userDetailsService;

    @Scheduled(fixedRate = 900000)
    public void handleScheduling() {
        try {
            List<User> usersList = userRepository.findByIsVerifiedFalse();
            List<UserDetails> userDetailsList = userDetailsRepository.findUserDetailsByUserIn(usersList);
            userDetailsRepository.deleteAll(userDetailsList);
            userRepository.deleteAll(usersList);
        } catch (Exception e) {
            log.info("Unable to delete records: ", e);
        }
    }
}