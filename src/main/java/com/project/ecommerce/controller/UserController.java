package com.project.ecommerce.controller;

import com.project.ecommerce.dto.*;
import com.project.ecommerce.entity.*;
import com.project.ecommerce.exception.GenericException;
import com.project.ecommerce.repository.ProductRepository;
import com.project.ecommerce.service.CartService;
import com.project.ecommerce.service.ProductService;
import com.project.ecommerce.service.UserDetailsService;
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

    private final ProductService productService;

    private final UserDetailsService userDetailsService;

    @GetMapping("/cart")
    public ResponseEntity<APISuccessResponse<List<ResponseGetCartItems>>> getCartItems(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        List<ResponseGetCartItems> list = new ArrayList<>();
        for(CartItem cartItem : user.getCart().getItems()){
            Product product = productService.getProductById(cartItem.getProductId()).orElse(null);
            ProductVariant productVariant = product.getProductVariants().stream().filter(variant -> variant.getId() == cartItem.getVariantId()).findFirst().get();
            ResponseGetCartItems cartItemDto = ResponseGetCartItems.builder()
                    .variantId(cartItem.getVariantId())
                    .quantity(cartItem.getQuantity())
                    .name(product.getName())
                    .description(product.getDescription())
                    .price(productVariant.getPrice())
                    .isAvailable(productVariant.getIsAvailable())
                    .imageUrls(productVariant.getImageUrls())
                    .build();
            list.add(cartItemDto);
        }
        return new ResponseEntity<>(APISuccessResponse.<List<ResponseGetCartItems>>builder().data(list).build(), HttpStatus.OK);
    }

//    @PostMapping("/cart")
//    public ResponseEntity<APISuccessResponse<?>> addToCart(@RequestBody CartItemDto cartItemDto, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) throws GenericException {
//        String token = authorizationHeader.substring(7);
//        User user = userService.findUserByToken(token);
//        cartService.addProduct(user, cartItemDto.getVariantId(), cartItemDto.getQuantity());
//        return new ResponseEntity<>(APISuccessResponse.builder().data(null).build(), HttpStatus.OK);
//    }
//
//    @DeleteMapping("/cart/{cart-item-id}")
//    public ResponseEntity<APISuccessResponse<?>> removeItemFromCart(@PathVariable("cart-item-id") Long itemId, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) throws GenericException{
//        String token = authorizationHeader.substring(7);
//        User user = userService.findUserByToken(token);
//        cartService.removeProduct(user, itemId);
//        return new ResponseEntity<>(APISuccessResponse.builder().data(null).build(), HttpStatus.OK);
//    }

    @PostMapping("/cart")
    public ResponseEntity<APISuccessResponse<?>> updateQuantity(@RequestBody UpdateCartItemQuantityDto updateCartItemQuantityDto, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) throws GenericException{
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        cartService.setProductQuantity(user, updateCartItemQuantityDto);
        return new ResponseEntity<>(APISuccessResponse.builder().data(null).build(), HttpStatus.OK);
    }

    @GetMapping("/user-details")
    public ResponseEntity<APISuccessResponse<UserDetailsDto>> getUserDetails(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        UserDetails userDetails = userDetailsService.findUserDetailsByUser(user);
        UserDetailsDto userDetailsDto = UserDetailsDto.builder()
                .title(userDetails.getTitle())
                .firstName(userDetails.getFirstName())
                .lastName(userDetails.getLastName())
                .phoneNumber(userDetails.getPhoneNumber())
                .email(userDetails.getEmail())
                .addressLine1(userDetails.getAddressLine1())
                .addressLine2(userDetails.getAddressLine2())
                .addressLine3(userDetails.getAddressLine3())
                .pincode(userDetails.getPincode())
                .build();
        return new ResponseEntity<>(APISuccessResponse.<UserDetailsDto>builder().data(userDetailsDto).build(), HttpStatus.OK);
    }
}