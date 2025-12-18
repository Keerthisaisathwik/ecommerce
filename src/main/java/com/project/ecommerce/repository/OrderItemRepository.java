package com.project.ecommerce.repository;

import com.project.ecommerce.entity.OrderItem;
import com.project.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    boolean existsByProductVariantIdAndUser(Long productVariantId, User user);
}
