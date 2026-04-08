package com.project.ecommerce.repository;

import com.project.ecommerce.dto.RatingCountDto;
import com.project.ecommerce.entity.Product;
import com.project.ecommerce.entity.ProductVariant;
import com.project.ecommerce.entity.Review;
import com.project.ecommerce.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByUserAndProduct(User user, Product product);

    @Query("""
    SELECT DISTINCT r
    FROM Review r
    LEFT JOIN r.imageUrls img
    WHERE r.product.id = :productId
    AND (:rating IS NULL OR r.rating = :rating)
    AND (:hasImages IS NULL OR :hasImages = false OR img IS NOT NULL)
""")
    Page<Review> findReviewsWithFilters(
            @Param("productId") Long productId,
            @Param("rating") Integer rating,
            @Param("hasImages") Boolean hasImages,
            Pageable pageable
    );

    @Query("""
    SELECT new com.project.ecommerce.dto.RatingCountDto(r.rating, COUNT(r))
    FROM Review r
    WHERE r.product.id = :productId
    GROUP BY r.rating
    ORDER BY r.rating
""")
    List<RatingCountDto> getRatingCountsByProductId(Long productId);
}
