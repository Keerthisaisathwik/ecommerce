package com.project.ecommerce.repository;

import com.project.ecommerce.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    boolean existsByVariantAsin(String variantAsin);

    Optional<ProductVariant> findByVariantAsin(String variantAsin);

    List<ProductVariant> findAllByVariantAsinIn(List<String> variantAsins);
}
