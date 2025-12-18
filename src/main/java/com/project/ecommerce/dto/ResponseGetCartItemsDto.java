package com.project.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseGetCartItemsDto {

    private List<CartItemDto> listOfCartItems;

    private double subTotal;

    private double discountedPrice;

    private double tax;

    private double shippingCharge;

    private double totalAmount;
}
