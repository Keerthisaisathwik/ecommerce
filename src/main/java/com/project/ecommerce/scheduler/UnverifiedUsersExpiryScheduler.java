package com.project.ecommerce.scheduler;

import com.project.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class UnverifiedUsersExpiryScheduler {

    private final UserRepository userRepository;

    @Scheduled(fixedRate = 900000) // 15 mins
    @Transactional
    public void cleanupUnverifiedUsers() {

        LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(15);

        int deleted = userRepository.deleteExpiredUnverifiedUsers(expiryTime);

        if (deleted > 0) {
            log.info("Deleted {} unverified users", deleted);
        }
    }
}