package com.project.ecommerce.repository;

import com.project.ecommerce.entity.User;
import com.project.ecommerce.entity.UserAddresses;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAddressesRepository extends JpaRepository<UserAddresses, Long> {

    List<UserAddresses> findByUser(User user);

    Optional<UserAddresses> findById(Long id);
}
