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
public class WishListItemDto {

    private long id;

    private long variantId;

    private String name;

    private String description;

    private Double price;

    private String imageUrl;

    private boolean isAvailable;
}
