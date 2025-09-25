package com.project.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @OneToOne
    @JoinColumn(name = "user")
    private User user;

    @NonNull
    private String firstName;

    @NonNull
    private String lastName;

    private String phoneNumber;

    private String email;

    private String address;

    private String pincode;
}
