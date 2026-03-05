package com.project.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(
        name = "order_item",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_order_variant",
                        columnNames = {"order_id", "product_variant_id"}
                )
        },
        indexes = {
                @Index(name = "idx_orderitem_variant_user", columnList = "product_variant_id, user_id"),
                @Index(name = "idx_orderitem_order_user", columnList = "order_id, user_id")
        }
)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Long productVariantId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private BigDecimal pricePerUnit;

    @Column(nullable = false)
    private BigDecimal unitPriceWithoutTax;

    @Column(nullable = false)
    private BigDecimal taxPerUnit;

    @Column(nullable = false)
    private BigDecimal taxRate;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal netAmount;

    @Column(nullable = false)
    private BigDecimal totalTaxAmount;

    @Column(nullable = false)
    private BigDecimal totalAmount;
}
