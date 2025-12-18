package com.project.ecommerce.entity;

import com.project.ecommerce.enums.OrderStatus;
import com.project.ecommerce.enums.PaymentMethod;
import com.project.ecommerce.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "orders",
        indexes = {
                @Index(name = "idx_order_user", columnList = "user_id"),
                @Index(name = "idx_order_status", columnList = "status"),
                @Index(name = "idx_order_created", columnList = "createdAt"),
                @Index(name = "idx_order_payment_status", columnList = "paymentStatus")
        })
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    private String paymentTransactionId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(nullable = false)
    private Double subtotal;

    @Column(nullable = false)
    private Double discountedPrice;

    @Column(nullable = false)
    private Double shippingCharge;

    @Column(nullable = false)
    private Double tax;

    @Column(nullable = false)
    private Double totalAmount;

    @Column(nullable = false)
    private String shippingAddress;

    @Column(nullable = false)
    private String billingAddress;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime paidAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.paidAt = LocalDateTime.now();
        this.shippedAt = LocalDateTime.now();
        this.deliveredAt = LocalDateTime.now();
    }
}
