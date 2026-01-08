package com.project.ecommerce.service.impl;

import com.project.ecommerce.dto.CartItemDto;
import com.project.ecommerce.dto.ResponseGetCartItemsDto;
import com.project.ecommerce.dto.SaveForLaterDto;
import com.project.ecommerce.dto.UpdateCartItemQuantityDto;
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
    public void removeProduct(User user, Long itemId){
        Cart cart = user.getCart();
        CartItem cartItem = cart.getItems().stream().filter(item -> item.getVariantId() == itemId).findFirst().orElse(null);
        if(cartItem == null){
            return;
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
                    .variantId(cartItem.getVariantId())
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
    @Transactional
    public void saveForLater(User user, SaveForLaterDto saveForLaterDto) throws GenericException {
        CartItem cartItem = cartItemRepository.findById(saveForLaterDto.getCartItemId()).orElseThrow(() -> new GenericException("cartItem not found"));
        if(!cartItem.getCart().getId().equals(user.getCart().getId())){
            throw new GenericException("Cannot access other user cart");
        }
        cartItem.setSaveForLater(saveForLaterDto.isSavedForLater());
        cartItemRepository.save(cartItem);
    }
}
