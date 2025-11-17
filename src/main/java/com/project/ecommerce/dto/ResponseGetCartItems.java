package com.project.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseGetCartItems {

    private long id;

    private long productId;

    private long variantId;

    private int quantity;

    private String name;

    private String description;

    private Double price;

    private String imageUrl;

    private boolean isAvailable;
}
