package com.project.ecommerce.repository;

import com.project.ecommerce.entity.Product;
import com.project.ecommerce.entity.ProductVariant;
import com.project.ecommerce.entity.Review;
import com.project.ecommerce.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByUserAndProduct(User user, Product product);

    Page<Review> findByProductVariant_Product_Id(Long productId, Pageable pageable);

    @Query("""
    SELECT DISTINCT r
    FROM Review r
    JOIN r.imageUrls img
    WHERE r.product.id = :productId
""")
    Page<Review> findReviewsWithImagesByProductId(
            @Param("productId") Long productId,
            Pageable pageable
    );
}
