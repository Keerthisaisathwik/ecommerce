package com.project.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddNewVariantToProduct {

    private Long productId;

    private Double price;

    private List<String> imageUrls;

    private Integer stockQuantity;
}