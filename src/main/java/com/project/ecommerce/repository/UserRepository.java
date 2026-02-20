package com.project.ecommerce.repository;

import com.project.ecommerce.entity.User;
import com.project.ecommerce.enums.AuthProviderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    User findByProviderTypeAndProviderId(AuthProviderType providerType, String provider);

    List<User> findByIsVerifiedFalse();

    @Modifying
    @Query("""
            DELETE FROM User u
            WHERE u.isVerified = false
            AND u.createdAt <= :expiryTime
            """)
    int deleteExpiredUnverifiedUsers(@Param("expiryTime") LocalDateTime expiryTime);
}
