package com.project.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductReviewDto {

    private String name;

    private int rating;

    private String reviewTitle;

    private String reviewMessage;

    private List<String> imageUrls;

    private List<VariantAttributeDto> variantAttributeList;

    private LocalDateTime updatedAt;
}
