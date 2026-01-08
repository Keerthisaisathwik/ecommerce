package com.project.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "user_addresses",
        indexes = {
                @Index(name = "idx_user_addresses_user", columnList = "user")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAddresses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user", nullable = false)
    private User user;

    private String addressLine1;

    private String addressLine2;

    private String addressLine3;

    private String pincode;
}
