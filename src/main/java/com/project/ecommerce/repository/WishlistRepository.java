package com.project.ecommerce.repository;

import com.project.ecommerce.entity.ProductVariant;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    Optional<Wishlist> findByUser(User user);

    void deleteByUserAndProductVariant(User user, ProductVariant product_variant);

    Optional<Wishlist> findByUserAndProductVariant(User user, ProductVariant product_variant);
}
