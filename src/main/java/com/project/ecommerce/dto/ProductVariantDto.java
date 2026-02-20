package com.project.ecommerce.dto;

import com.project.ecommerce.entity.Product;
import com.project.ecommerce.enums.CategoryType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductVariantDto {

    private String variantAsin;

    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;

    private String imageUrl;

    @Builder.Default
    private BigDecimal discountedPrice = BigDecimal.ZERO;

    private Integer stockQuantity;

    @Builder.Default
    private Boolean isAvailable = true;

    private List<VariantAttributeDto> variantAttributeList;
}
