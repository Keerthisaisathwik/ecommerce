package com.project.ecommerce.service;

import com.project.ecommerce.dto.WishListItemDto;
import com.project.ecommerce.entity.ProductVariant;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.exception.GenericException;

import java.util.List;

public interface WishlistService {
    List<WishListItemDto> getAllWishlistItems(User user);

    void addToWishlist(User user, Long variantId) throws GenericException;

    void removeFromWishlist(User user, Long variantId) throws GenericException;

    boolean existsByUserAndProductVariant(User user, ProductVariant productVariant);
}
