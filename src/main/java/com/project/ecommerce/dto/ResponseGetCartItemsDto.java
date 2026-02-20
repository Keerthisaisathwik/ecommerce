package com.project.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseGetCartItemsDto {

    private List<CartItemDto> listOfCartItems;

    @Builder.Default
    private BigDecimal withoutDiscountPrice = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal discountedPrice = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal shippingCharge = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;
}
