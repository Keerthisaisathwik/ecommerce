package com.project.ecommerce.repository;

import com.project.ecommerce.entity.User;
import com.project.ecommerce.enums.AuthProviderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    User findByProviderTypeAndProviderId(AuthProviderType providerType, String provider);
}
