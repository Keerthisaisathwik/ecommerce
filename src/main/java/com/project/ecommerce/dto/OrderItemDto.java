package com.project.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemDto {

    private String variantAsin;

    private int quantity;

    private String name;

    private String description;

    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;

    private String imageUrl;

    private BigDecimal totalAmount;
}
