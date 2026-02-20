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
public class GetCategoryProductsDTO {

    private String variantAsin;

    private String imageUrl;

    private String title;

    private Double rating;

    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;
}
