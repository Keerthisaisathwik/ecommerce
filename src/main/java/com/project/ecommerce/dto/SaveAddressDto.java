package com.project.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaveAddressDto {

    private String addressLine1;

    private String addressLine2;

    private String addressLine3;

    private String pincode;

    private String name;

    private Long phoneNumber;

    private Long alternativePhoneNumber;

    private String country;

    private String state;

    private String city;
}
