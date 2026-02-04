package com.project.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetCategoryProductsDTO {

    private String variantAsin;

    private String imageUrl;

    private String title;

    private Double rating;

    private Double price;
}
