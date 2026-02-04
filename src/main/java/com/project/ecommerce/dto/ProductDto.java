package com.project.ecommerce.dto;

import com.project.ecommerce.entity.ProductVariant;
import com.project.ecommerce.enums.CategoryType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {

    private String productVariantAsin;

    private CategoryType category;

    private String name;

    private String description;

    private String brand;

    private Double price;

    private List<String> imageUrls;

    private Double averageRating;

    private Integer totalReviews;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<ProductVariantDto> productVariants;

    private Integer stockQuantity;

    private Boolean isPreviouslyOrdered;

    private Integer cartQuantity;

    private Boolean isWishlisted;
}
