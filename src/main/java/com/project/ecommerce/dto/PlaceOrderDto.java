package com.project.ecommerce.dto;

import com.project.ecommerce.entity.User;
import com.project.ecommerce.enums.OrderStatus;
import com.project.ecommerce.enums.PaymentMethod;
import com.project.ecommerce.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlaceOrderDto {

    private PaymentMethod paymentMethod;

    private String shippingAddress;

    private String billingAddress;
}