package com.project.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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

    @JsonProperty("isAvailable")
    private boolean isAvailable;

    private Double discountedPrice;

    private LocalDateTime addedAt;
}
