package com.project.ecommerce.repository;

import com.project.ecommerce.entity.Product;
import com.project.ecommerce.enums.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findById(Long id);

    Optional<Page<Product>> findByCategory(CategoryType categoryType, Pageable pageable);

    Optional<List<Product>> findByCategory(CategoryType categoryType);
}