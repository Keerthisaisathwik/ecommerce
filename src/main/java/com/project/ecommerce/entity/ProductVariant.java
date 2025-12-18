package com.project.ecommerce.entity;

import com.project.ecommerce.enums.CategoryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Double discountedPrice;

    @Column(nullable = false)
    @DecimalMin(value = "0.0", inclusive = false, message = "Tax must be greater than 0")
    @DecimalMax(value = "100.0", inclusive = false, message = "Tax must be less than 100")
    private Double taxPercentage;

    @ElementCollection
    @CollectionTable(name = "product_variant_images", joinColumns = @JoinColumn(name = "product_variant_id"))
    @Column(name = "image_url")
    @OrderColumn(name = "image_order")
    private List<String> imageUrls;

    @Column(nullable = false)
    private Integer stockQuantity;

    @Builder.Default
    private Boolean isAvailable = true;

    @PrePersist
    public void prePersist() {
        if (isAvailable == null) this.isAvailable = true;
        if (stockQuantity != null) this.isAvailable = stockQuantity > 0;
    }

    @PreUpdate
    public void preUpdate() {
        if (stockQuantity != null) this.isAvailable = stockQuantity > 0;
    }
}