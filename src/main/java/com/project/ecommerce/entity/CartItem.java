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
                @Index(name = "idx_cart_variant", columnList = "cart_id, variantId")
        },
        uniqueConstraints = @UniqueConstraint(columnNames = {"cart_id", "variantId"})
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

    private int quantity;

    @Column(name = "save_for_later", nullable = false)
    private boolean saveForLater = false;
}
