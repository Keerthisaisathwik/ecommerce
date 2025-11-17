package com.project.ecommerce.service;

import com.project.ecommerce.dto.CartItemDto;
import com.project.ecommerce.dto.UpdateCartItemQuantityDto;
import com.project.ecommerce.entity.CartItem;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.exception.GenericException;

import java.util.Set;

public interface CartService {
    public void addProduct(User user, CartItemDto cartItemDto) throws GenericException;

    public void removeProduct(User user, Long itemId)  throws GenericException;

    public void setProductQuantity(User user, UpdateCartItemQuantityDto updateCartItemQuantityDto) throws GenericException;
}