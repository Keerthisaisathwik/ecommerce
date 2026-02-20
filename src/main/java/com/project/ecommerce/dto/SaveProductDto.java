package com.project.ecommerce.dto;

import com.project.ecommerce.entity.Product;
import com.project.ecommerce.enums.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaveProductDto {

    private CategoryType category;

    private String name;

    private String description;

    private String brand;

    private List<SaveProductVariantDto> productVariants;
}
