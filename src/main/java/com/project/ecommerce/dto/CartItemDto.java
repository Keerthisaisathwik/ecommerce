package com.project.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemDto {

    private long variantId;

    private int quantity;

    private String name;

    private String description;

    private Double price;

    private List<String> imageUrls;

    @JsonProperty("isAvailable")
    private boolean isAvailable;

    private boolean saveForLater;
}
