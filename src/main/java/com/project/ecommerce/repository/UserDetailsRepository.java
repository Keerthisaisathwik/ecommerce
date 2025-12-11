package com.project.ecommerce.repository;

import com.project.ecommerce.entity.User;
import com.project.ecommerce.entity.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserDetailsRepository extends JpaRepository<UserDetails, Long> {
    Optional<UserDetails> findByUser(User user);

    Optional<UserDetails> findByEmail(String email);

    List<UserDetails> findUserDetailsByUserIn(List<User> usersList);
}
