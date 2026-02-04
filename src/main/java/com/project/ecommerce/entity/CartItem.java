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
        indexes = @Index(name = "idx_cart_variant_asin", columnList = "cart_id, variant_asin"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"cart_id", "variant_asin"})
)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    private long productId;

    private long variantId;

    @Column(name = "variant_asin", nullable = false, length = 20)
    private String variantAsin;

    @Builder.Default
    private int quantity = 1;

    @Builder.Default
    @Column(name = "save_for_later", nullable = false)
    private boolean saveForLater = false;
}
