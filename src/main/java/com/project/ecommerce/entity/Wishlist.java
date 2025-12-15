package com.project.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(
        indexes = {
                @Index(name = "idx_wishlist_user_id", columnList = "user_id"),
                @Index(
                        name = "idx_wishlist_user_product_variant",
                        columnList = "user_id,product_variant_id"
                )
        },
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id","product_variant_id"}
        )
)
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariant productVariant;
}

