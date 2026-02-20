package com.project.ecommerce.entity;

import com.project.ecommerce.enums.CategoryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    private BigDecimal price;

    @Column(nullable = false)
    private BigDecimal discountedPrice;

    @Column(nullable = false)
    @DecimalMin(value = "0.0", inclusive = false, message = "Tax must be greater than 0")
    @DecimalMax(value = "100.0", inclusive = false, message = "Tax must be less than 100")
    private BigDecimal taxPercentage;

    @ElementCollection
    @CollectionTable(name = "product_variant_images", joinColumns = @JoinColumn(name = "product_variant_id"))
    @Column(name = "image_url")
    @OrderColumn(name = "image_order")
    private List<String> imageUrls;

    @Column(nullable = false)
    private Integer stockQuantity;

    @Builder.Default
    private Boolean isAvailable = true;

    @Column(unique = true, nullable = false)
    private String variantAsin;

    @OneToMany(
            mappedBy = "variant",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<VariantAttribute> attributes = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (isAvailable == null) this.isAvailable = true;
        if (stockQuantity != null) this.isAvailable = stockQuantity > 0;
    }

    @PreUpdate
    public void preUpdate() {
        if (stockQuantity != null) this.isAvailable = stockQuantity > 0;
    }

    public void addAttribute(VariantAttribute attribute) {
        attributes.add(attribute);
        attribute.setVariant(this);
    }

    public void removeAttribute(VariantAttribute attribute) {
        attributes.remove(attribute);
        attribute.setVariant(null);
    }

    public String getFormattedAttributesKey() {
        return this.attributes.stream()
                .sorted(Comparator.comparing(VariantAttribute::getAttributeName))
                .map(attr -> attr.getAttributeName() + "=" + attr.getAttributeValue())
                .collect(Collectors.joining("|"));
    }
}