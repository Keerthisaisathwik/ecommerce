package com.project.ecommerce.repository;

import com.project.ecommerce.entity.Order;
import com.project.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findById(Long orderId);

    Optional<List<Order>> findByUser(User user);
}
