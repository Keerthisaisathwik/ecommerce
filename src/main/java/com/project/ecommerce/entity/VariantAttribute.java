package com.project.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"productVariant_id", "attributeName"}
        )
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VariantAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productVariant_id", nullable = false)
    private ProductVariant variant;

    @Column(nullable = false)
    private String attributeName;

    @Column(nullable = false)
    private String attributeValue;
}
