package com.project.ecommerce.repository;

import com.project.ecommerce.entity.Cart;
import com.project.ecommerce.entity.CartItem;
import com.project.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("""
        SELECT ci FROM CartItem ci
        WHERE ci.cart.user.id = :userId
        AND ci.variantId = :variantId
    """)
    Optional<CartItem> findCartItem(Long userId, Long variantId);

    @Modifying
    @Transactional
    @Query("""
        DELETE FROM CartItem ci
        WHERE ci.cart.id = :cartId
        AND ci.variantId IN :variantIds
    """)
    void deleteByCartIdAndVariantIds(Long cartId, List<Long> variantIds);

}
