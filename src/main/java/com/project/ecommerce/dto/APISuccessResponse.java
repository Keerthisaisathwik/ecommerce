package com.project.ecommerce.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class APISuccessResponse<T> {
    private Boolean status=true;
    private String message=null;
    private T data;
}