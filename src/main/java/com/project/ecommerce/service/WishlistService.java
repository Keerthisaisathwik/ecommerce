package com.project.ecommerce.service;

import com.project.ecommerce.dto.WishListItemDto;
import com.project.ecommerce.entity.ProductVariant;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.entity.Wishlist;
import com.project.ecommerce.exception.GenericException;

import java.util.List;

public interface WishlistService {
    List<Wishlist> getAllWishlistItems(User user);

    void addToWishlist(User user, String variantAsin) throws GenericException;

    void removeFromWishlist(User user, String variantAsin) throws GenericException;

    boolean existsByUserAndProductVariant(User user, ProductVariant productVariant);
}
