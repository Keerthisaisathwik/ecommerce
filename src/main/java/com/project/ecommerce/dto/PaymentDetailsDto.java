package com.project.ecommerce.dto;

import com.project.ecommerce.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDetailsDto {
    private PaymentMethod paymentMethod;

    private BigDecimal price;

    private String deliveryAddress;
}
