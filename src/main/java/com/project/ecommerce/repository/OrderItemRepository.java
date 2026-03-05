package com.project.ecommerce.repository;

import com.project.ecommerce.entity.Order;
import com.project.ecommerce.entity.OrderItem;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    boolean existsByProductVariantIdAndUserAndOrder_Status(
            Long productVariantId,
            User user,
            OrderStatus status
    );

    List<OrderItem> findByOrder(Order order);
}
