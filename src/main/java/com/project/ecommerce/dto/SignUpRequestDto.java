package com.project.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestDto {
    String title;
    String firstName;
    String lastName;
    String phoneNumber;
    String email;
    String address;
    String pincode;
}
