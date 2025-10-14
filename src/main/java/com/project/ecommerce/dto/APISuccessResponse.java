package com.project.ecommerce.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class APISuccessResponse<T> {
    private final Boolean status=true;
    private final String message=null;
    private T data;
}