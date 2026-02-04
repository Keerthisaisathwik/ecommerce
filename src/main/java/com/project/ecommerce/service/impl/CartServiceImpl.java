package com.project.ecommerce.service.impl;

import com.project.ecommerce.dto.*;
import com.project.ecommerce.entity.*;
import com.project.ecommerce.exception.GenericException;
import com.project.ecommerce.repository.*;
import com.project.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final UserRepository userRepository;

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    private final ProductVariantRepository productVariantRepository;

    private final ProductRepository productRepository;

    @Override
    public void addProduct(User user, String variantAsin, int quantity) throws GenericException {
        ProductVariant productVariant = productVariantRepository.findByVariantAsin(variantAsin).orElse(null);
        if(productVariant == null){
            throw new GenericException("Product Variant not found");
        }
        CartItem newCartItem = CartItem.builder()
                .cart(user.getCart())
                .productId(productVariant.getProduct().getId())
                .variantId(productVariant.getId())
                .variantAsin(variantAsin)
                .quantity(quantity)
                .build();
        cartItemRepository.save(newCartItem);
    }

    @Override
    public void removeProduct(User user, String variantAsin){
        Cart cart = user.getCart();
        CartItem cartItem = cart.getItems().stream().filter(item -> item.getVariantAsin().equals(variantAsin)).findFirst().orElse(null);
        if(cartItem == null){
            return;
        }
        cart.getItems().remove(cartItem);
        cartRepository.save(cart);
    }

    @Override
    public void setProductQuantity(User user, UpdateCartItemQuantityDto updateCartItemQuantityDto) throws GenericException{
        Cart cart = user.getCart();
        CartItem cartItem =
                cart.getItems().stream().filter(item -> item.getVariantAsin().equals(updateCartItemQuantityDto.getVariantAsin())).findFirst().orElse(null);
        if(updateCartItemQuantityDto.getQuantity() == 0){
            removeProduct(user, updateCartItemQuantityDto.getVariantAsin());
        } else if (cartItem == null){
            addProduct(user, updateCartItemQuantityDto.getVariantAsin(), updateCartItemQuantityDto.getQuantity());
        } else {
            cartItem.setQuantity(updateCartItemQuantityDto.getQuantity());
            cartItemRepository.save(cartItem);
        }
    }

    @Override
    public int findSpecificCartItemQuantity(Long userId, Long variantId) {
        return cartItemRepository.findCartItem(userId, variantId).map(cartItem -> cartItem.getQuantity()).orElse(0);
    }

    @Override
    public ResponseGetCartItemsDto getCartDetails(User user) {
        ResponseGetCartItemsDto responseGetCartItemsDto = new ResponseGetCartItemsDto();
        List<CartItemDto> list = new ArrayList<>();
        for(CartItem cartItem : user.getCart().getItems()){
            Product product = productRepository.findById(cartItem.getProductId()).orElse(null);
            ProductVariant productVariant = product.getProductVariants().stream().filter(variant -> variant.getId() == cartItem.getVariantId()).findFirst().get();
            CartItemDto cartItemDto = CartItemDto.builder()
                    .id(cartItem.getId())
                    .variantAsin(cartItem.getVariantAsin())
                    .quantity(cartItem.getQuantity())
                    .name(product.getName())
                    .description(product.getDescription())
                    .price(productVariant.getPrice())
                    .isAvailable(productVariant.getIsAvailable())
                    .imageUrls(productVariant.getImageUrls())
                    .saveForLater(cartItem.isSaveForLater())
                    .discountedPrice(productVariant.getDiscountedPrice())
                    .build();
            list.add(cartItemDto);
            if(!cartItem.isSaveForLater()){
                responseGetCartItemsDto.setSubTotal(responseGetCartItemsDto.getSubTotal() + (cartItemDto.getQuantity() * productVariant.getPrice()));
                responseGetCartItemsDto.setDiscountedPrice(responseGetCartItemsDto.getDiscountedPrice() + (cartItemDto.getQuantity() * productVariant.getDiscountedPrice()));
                responseGetCartItemsDto.setTax(responseGetCartItemsDto.getTax() + (cartItemDto.getQuantity() * (productVariant.getDiscountedPrice() * (productVariant.getTaxPercentage() / 100))));
            }
        }
        responseGetCartItemsDto.setListOfCartItems(list);
        responseGetCartItemsDto.setTotalAmount(responseGetCartItemsDto.getDiscountedPrice()+responseGetCartItemsDto.getTax());
        responseGetCartItemsDto.setShippingCharge(responseGetCartItemsDto.getTotalAmount() == 0 || responseGetCartItemsDto.getTotalAmount() >= 500 ? 0.0 : 40.0);
        responseGetCartItemsDto.setTax(Math.round(responseGetCartItemsDto.getTax() * 100.0) / 100.0);
        return responseGetCartItemsDto;
    }

    @Override
    public void saveForLater(User user, SaveForLaterDto saveForLaterDto) throws GenericException {
        CartItem cartItem = cartItemRepository.findById(saveForLaterDto.getCartItemId()).orElseThrow(() -> new GenericException("cartItem not found"));
        if(!cartItem.getCart().getId().equals(user.getCart().getId())){
            throw new GenericException("Cannot access other user cart");
        }
        cartItem.setSaveForLater(saveForLaterDto.isSavedForLater());
        cartItemRepository.save(cartItem);
    }

    @Override
    public void addToCartAndSaveForLater(User user, AddToCartAndSaveForLaterDto addToCartAndSaveForLaterDto) throws GenericException {
        if(addToCartAndSaveForLaterDto.getQuantity() == 0){
            return;
        }
        Cart cart = user.getCart();
        CartItem cartItem =
                cart.getItems().stream().filter(item -> item.getVariantAsin().equals(addToCartAndSaveForLaterDto.getVariantAsin())).findFirst().orElse(null);
        if (cartItem == null){
            ProductVariant productVariant = productVariantRepository.findByVariantAsin(addToCartAndSaveForLaterDto.getVariantAsin()).orElse(null);
            if(productVariant == null){
                throw new GenericException("Product Variant not found");
            }
            CartItem newCartItem = CartItem.builder()
                    .cart(user.getCart())
                    .productId(productVariant.getProduct().getId())
                    .variantId(productVariant.getId())
                    .variantAsin(addToCartAndSaveForLaterDto.getVariantAsin())
                    .quantity(addToCartAndSaveForLaterDto.getQuantity())
                    .saveForLater(true)
                    .build();
            cartItemRepository.save(newCartItem);
        } else {
            cartItem.setQuantity(addToCartAndSaveForLaterDto.getQuantity());
            cartItem.setSaveForLater(true);
            cartItemRepository.save(cartItem);
        }
    }
}
