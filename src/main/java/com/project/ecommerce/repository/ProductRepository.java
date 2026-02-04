package com.project.ecommerce.repository;

import com.project.ecommerce.entity.Product;
import com.project.ecommerce.enums.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findById(Long id);

    Page<Product> findByCategory(CategoryType categoryType, Pageable pageable);

    @Query("""
    SELECT p FROM Product p 
    WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))
       OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))
       OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    Page<Product> searchProducts(@Param("query") String query, Pageable pageable);

}