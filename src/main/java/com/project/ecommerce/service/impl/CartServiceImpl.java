package com.project.ecommerce.service.impl;

import com.project.ecommerce.dto.CartItemDto;
import com.project.ecommerce.dto.UpdateCartItemQuantityDto;
import com.project.ecommerce.entity.Cart;
import com.project.ecommerce.entity.CartItem;
import com.project.ecommerce.entity.ProductVariant;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.exception.GenericException;
import com.project.ecommerce.repository.CartItemRepository;
import com.project.ecommerce.repository.CartRepository;
import com.project.ecommerce.repository.ProductVariantRepository;
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

    private final ProductVariantRepository productVariantRepository;

    @Override
    public void addProduct(User user, Long variantId, int quantity) throws GenericException {
        ProductVariant productVariant = productVariantRepository.findById(variantId).orElse(null);
        if(productVariant == null){
            throw new GenericException("Product Variant not found");
        }
        CartItem newCartItem = CartItem.builder()
                .cart(user.getCart())
                .productId(productVariant.getProduct().getId())
                .variantId(variantId)
                .quantity(quantity)
                .build();
        Cart cart = user.getCart();
        cart.getItems().add(newCartItem);
        cartRepository.save(cart);
    }

    @Override
    public void removeProduct(User user, Long itemId) throws GenericException{
        Cart cart = user.getCart();
        CartItem cartItem = cart.getItems().stream().filter(item -> item.getVariantId() == itemId).findFirst().orElse(null);
        if(cartItem == null){
            throw new GenericException("Could not find the item in cart to remove it");
        }
        cart.getItems().remove(cartItem);
        cartRepository.save(cart);
    }

    @Override
    public void setProductQuantity(User user, UpdateCartItemQuantityDto updateCartItemQuantityDto) throws GenericException{
        Cart cart = user.getCart();
        CartItem cartItem = cart.getItems().stream().filter(item -> item.getVariantId() == updateCartItemQuantityDto.getVariantId()).findFirst().orElse(null);
        if(updateCartItemQuantityDto.getQuantity() == 0){
            removeProduct(user, updateCartItemQuantityDto.getVariantId());
        } else if (cartItem == null){
            addProduct(user, updateCartItemQuantityDto.getVariantId(), updateCartItemQuantityDto.getQuantity());
        } else {
            cartItem.setQuantity(updateCartItemQuantityDto.getQuantity());
            cartItemRepository.save(cartItem);
        }
    }
}
