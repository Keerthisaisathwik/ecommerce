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
public class SaveProductVariantDto {

    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;

    private List<String> imageUrls;

    private Integer stockQuantity;

    @Builder.Default
    private Boolean isAvailable = true;

    @Builder.Default
    private BigDecimal taxPercentage = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal discountedPrice = BigDecimal.ZERO;

    private List<VariantAttributeRequestDto> variantAttributeList;
}
