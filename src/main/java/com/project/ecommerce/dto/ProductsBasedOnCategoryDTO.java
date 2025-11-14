package com.project.ecommerce.dto;

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
public class ProductsBasedOnCategoryDTO {

    private CategoryType category;

    private List<ProductDto> products;
}