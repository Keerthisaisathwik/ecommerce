package com.project.ecommerce.service;

import com.project.ecommerce.dto.ResponseGetCartItemsDto;
import com.project.ecommerce.dto.UpdateCartItemQuantityDto;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.exception.GenericException;

public interface CartService {
    public void addProduct(User user, Long variantId, int quantity) throws GenericException;

    public void removeProduct(User user, Long itemId);

    public void setProductQuantity(User user, UpdateCartItemQuantityDto updateCartItemQuantityDto) throws GenericException;

    public int findSpecificCartItemQuantity(Long userId, Long variantId);

    public ResponseGetCartItemsDto getCartDetails(User user);
}