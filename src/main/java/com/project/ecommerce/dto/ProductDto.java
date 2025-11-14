package com.project.ecommerce.dto;

import com.project.ecommerce.enums.CategoryType;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {

    private CategoryType category;

    private String name;

    private String description;

    private String brand;

    private Double price;

    private String imageUrl;

    private Integer stockQuantity;

    private Boolean isAvailable;
}
