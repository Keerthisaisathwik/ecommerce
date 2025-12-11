package com.project.ecommerce.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserDetailsDto {
    String title;
    String firstName;
    String lastName;
    String phoneNumber;
    String email;
    String addressLine1;
    String addressLine2;
    String addressLine3;
    String pincode;
}
