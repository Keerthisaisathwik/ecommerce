package com.project.ecommerce.dto;

import com.project.ecommerce.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlaceSingleOrderDto {

    private String variantAsin;

    private int quantity;

    private PaymentMethod paymentMethod;

    private String shippingAddress;

    private String billingAddress;
}
