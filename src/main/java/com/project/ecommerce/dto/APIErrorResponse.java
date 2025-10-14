package com.project.ecommerce.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class APIErrorResponse<T> {
    private final Boolean status=false;
    private final String data=null;
    private String Message;
}