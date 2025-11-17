package com.project.ecommerce.service.impl;

import com.project.ecommerce.dto.CartItemDto;
import com.project.ecommerce.dto.UpdateCartItemQuantityDto;
import com.project.ecommerce.entity.Cart;
import com.project.ecommerce.entity.CartItem;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.exception.GenericException;
import com.project.ecommerce.repository.CartItemRepository;
import com.project.ecommerce.repository.CartRepository;
import com.project.ecommerce.repository.UserRepository;
import com.project.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final UserRepository userRepository;

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    @Override
    public void addProduct(User user, CartItemDto cartItemDto) throws GenericException {
        CartItem newCartItem = CartItem.builder()
                .cart(user.getCart())
                .productId(cartItemDto.getProductId())
                .variantId(cartItemDto.getProductId())
                .quantity(cartItemDto.getQuantity())
                .build();
        Cart cart = user.getCart();
        cart.getItems().add(newCartItem);
        cartRepository.save(cart);
    }

    @Override
    public void removeProduct(User user, Long itemId) throws GenericException{
        Cart cart = user.getCart();
        CartItem cartItem = cart.getItems().stream().filter(item -> item.getId().equals(itemId)).findFirst().orElse(null);
        if(cartItem == null){
            throw new GenericException("invalid cart item id");
        }
        cart.getItems().remove(cartItem);
        cartRepository.save(cart);
    }

    @Override
    public void setProductQuantity(User user, UpdateCartItemQuantityDto updateCartItemQuantityDto) throws GenericException{
        Cart cart = user.getCart();
        CartItem cartItem = cart.getItems().stream().filter(item -> item.getId().equals(updateCartItemQuantityDto.getItemId())).findFirst().orElse(null);
        if(cartItem == null){
            throw new GenericException("invalid cart item id");
        }
        cartItem.setQuantity(updateCartItemQuantityDto.getQuantity());
        cartItemRepository.save(cartItem);
    }
}
