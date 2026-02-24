package com.project.ecommerce.repository;

import com.project.ecommerce.entity.Order;
import com.project.ecommerce.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findById(Long orderId);

    Optional<List<Order>> findByUser(User user);

    @Modifying
    @Query("""
            UPDATE Order o
            SET o.status = 'CANCELLED'
            WHERE o.status = 'PENDING_PAYMENT'
            AND o.createdAt <= :expiryTime
            """)
    int cancelExpiredOrders(@Param("expiryTime") LocalDateTime expiryTime);

    Optional<Order> findByUserAndPaymentToken(User user, String paymentMethod);
}
