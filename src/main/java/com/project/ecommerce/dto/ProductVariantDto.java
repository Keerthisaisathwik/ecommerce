package com.project.ecommerce.dto;

import com.project.ecommerce.entity.Product;
import com.project.ecommerce.enums.CategoryType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductVariantDto {

    private Long id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Double price;

    private List<String> imageUrls;

    private Integer stockQuantity;

    private Boolean isAvailable = true;
}
