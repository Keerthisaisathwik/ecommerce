package com.project.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItem {

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    private long productId;

    @Id
    private long variantId;

    private int quantity;
}
