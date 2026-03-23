package com.project.ecommerce.repository;

import com.project.ecommerce.entity.Order;
import com.project.ecommerce.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findById(Long orderId);

    Page<Order> findByUser(User user, Pageable pageable);

    @Modifying
    @Query("""
            UPDATE Order o
            SET o.status = 'CANCELLED'
            WHERE o.status = 'PENDING_PAYMENT'
            AND o.createdAt <= :expiryTime
            """)
    int cancelExpiredOrders(@Param("expiryTime") LocalDateTime expiryTime);

    Optional<Order> findByUserAndPaymentToken(User user, String paymentMethod);

    Optional<Order> findByOrderNumber(String orderNumber);

    @Query("""
            SELECT o FROM Order o 
            WHERE 
                (o.status = 'PAID' AND o.paidAt <= :time1)
             OR (o.status = 'SHIPPED' AND o.shippedAt <= :time2)
             OR (o.status = 'OUT_FOR_DELIVERY' AND o.outForDeliveryAt <= :time3)
            """)
    List<Order> findOrdersReadyForNextStep(@Param("time1") LocalDateTime time1,
                                           @Param("time2") LocalDateTime time2,
                                           @Param("time3") LocalDateTime time3
    );
}
