package com.project.ecommerce.controller;

import com.project.ecommerce.dto.APISuccessResponse;
import com.project.ecommerce.dto.CartItemDto;
import com.project.ecommerce.dto.ResponseCartItemDto;
import com.project.ecommerce.dto.UpdateCartItemQuantityDto;
import com.project.ecommerce.entity.CartItem;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.exception.GenericException;
import com.project.ecommerce.service.CartService;
import com.project.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final CartService cartService;

    @GetMapping("/cart")
    public ResponseEntity<APISuccessResponse<List<ResponseCartItemDto>>> getCartItems(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        List<ResponseCartItemDto> list = new ArrayList<>();
        for(CartItem cartItem : user.getCart().getItems()){
            ResponseCartItemDto cartItemDto = ResponseCartItemDto.builder()
                    .id(cartItem.getId())
                    .productId(cartItem.getProductId())
                    .variantId(cartItem.getVariantId())
                    .quantity(cartItem.getQuantity())
                    .build();
            list.add(cartItemDto);
        }
        return new ResponseEntity<>(APISuccessResponse.<List<ResponseCartItemDto>>builder().data(list).build(), HttpStatus.OK);
    }

    @PostMapping("/add-product")
    public ResponseEntity<APISuccessResponse<?>> addToCart(@RequestBody CartItemDto cartItemDto, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) throws GenericException {
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        cartService.addProduct(user, cartItemDto);
        return new ResponseEntity<>(APISuccessResponse.builder().data(null).build(), HttpStatus.OK);
    }

    @DeleteMapping("/remove-product/{cart-item-id}")
    public ResponseEntity<APISuccessResponse<?>> removeItemFromCart(@PathVariable("cart-item-id") Long itemId, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) throws GenericException{
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        cartService.removeProduct(user, itemId);
        return new ResponseEntity<>(APISuccessResponse.builder().data(null).build(), HttpStatus.OK);
    }

    @PatchMapping("/update-quantity")
    public ResponseEntity<APISuccessResponse<?>> updateQuantity(@RequestBody UpdateCartItemQuantityDto updateCartItemQuantityDto, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) throws GenericException{
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        cartService.setProductQuantity(user, updateCartItemQuantityDto);
        return new ResponseEntity<>(APISuccessResponse.builder().data(null).build(), HttpStatus.OK);
    }
}