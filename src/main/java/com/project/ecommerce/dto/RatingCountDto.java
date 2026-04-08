package com.project.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RatingCountDto {
    // @Builder I am intentionally not using the builder because JPA cannot use this.
    // @NoArgsConstructor better to remove it.
    private int rating;
    private Long count;
}
