package com.project.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class WishListItemDto {

    private long id;

    private String variantAsin;

    private String name;

    private String description;

    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;

    private String imageUrl;

    @JsonProperty("isAvailable")
    private boolean isAvailable;

    @Builder.Default
    private BigDecimal discountedPrice = BigDecimal.ZERO;

    private LocalDateTime addedAt;
}
