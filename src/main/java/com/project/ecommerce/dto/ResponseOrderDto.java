package com.project.ecommerce.dto;

import com.project.ecommerce.enums.OrderStatus;
import com.project.ecommerce.enums.PaymentMethod;
import com.project.ecommerce.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseOrderDto {
    private Long id;

    private String orderNumber;

    private OrderStatus status;

    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus;

    private String paymentTransactionId;

    private Double subtotal;

    private Double discountedPrice;

    private Double shippingCharge;

    private Double tax;

    private Double totalAmount;

    private String shippingAddress;

    private String billingAddress;

    private LocalDateTime createdAt;

    private LocalDateTime paidAt;

    private LocalDateTime shippedAt;

    private LocalDateTime deliveredAt;
}
