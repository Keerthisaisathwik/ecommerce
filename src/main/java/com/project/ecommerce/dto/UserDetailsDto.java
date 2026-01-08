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
}
