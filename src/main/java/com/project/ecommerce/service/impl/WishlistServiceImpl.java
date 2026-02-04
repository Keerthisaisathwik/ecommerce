package com.project.ecommerce.service.impl;

import com.project.ecommerce.dto.WishListItemDto;
import com.project.ecommerce.entity.ProductVariant;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.entity.Wishlist;
import com.project.ecommerce.exception.GenericException;
import com.project.ecommerce.repository.CartItemRepository;
import com.project.ecommerce.repository.ProductVariantRepository;
import com.project.ecommerce.repository.WishlistRepository;
import com.project.ecommerce.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WishlistServiceImpl implements WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Override
    public List<WishListItemDto> getAllWishlistItems(User user) {
        List<WishListItemDto> wishlist = wishlistRepository.findByUser(user).stream()
                .map((item) ->
                {
                    ProductVariant productVariant = item.getProductVariant();
                    return WishListItemDto.builder()
                            .id(item.getId())
                            .variantAsin(productVariant.getVariantAsin())
                            .name(productVariant.getProduct().getName())
                            .description(productVariant.getProduct().getDescription())
                            .price(productVariant.getPrice())
                            .imageUrl(productVariant.getImageUrls().getFirst())
                            .isAvailable(productVariant.getIsAvailable())
                            .discountedPrice(productVariant.getDiscountedPrice())
                            .addedAt(item.getAddedAt())
                            .build();
                }).toList();
        System.out.println(wishlist);
        return wishlist;
    }

    @Override
    public void addToWishlist(User user, String variantAsin) throws GenericException {
        ProductVariant productVariant = productVariantRepository.findByVariantAsin(variantAsin).orElse(null);
        if(productVariant == null)
            throw new GenericException("Variant Id : "+ variantAsin +" is not present in the database please recheck the variant asin");
        if(wishlistRepository.findByUserAndProductVariant(user, productVariant).isPresent())
            return;
        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setProductVariant(productVariant);
        wishlistRepository.save(wishlist);
    }

    @Transactional
    @Override
    public void removeFromWishlist(User user, String variantAsin) throws GenericException {
        ProductVariant productVariant = productVariantRepository.findByVariantAsin(variantAsin).orElse(null);
        if(productVariant == null)
            throw new GenericException("Variant Id : "+ variantAsin +" is not present in the database please recheck the variant asin");
        wishlistRepository.deleteByUserAndProductVariant(user, productVariant);
    }

    @Override
    public boolean existsByUserAndProductVariant(User user, ProductVariant productVariant) {
        return wishlistRepository.findByUserAndProductVariant(user,productVariant).isPresent();
    }
}
