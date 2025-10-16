package com.project.ecommerce.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyEmailDTO {

    private String token;
    private String username;
    private String password;
}
