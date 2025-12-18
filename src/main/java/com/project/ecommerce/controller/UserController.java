package com.project.ecommerce.controller;

import com.project.ecommerce.dto.*;
import com.project.ecommerce.entity.*;
import com.project.ecommerce.exception.GenericException;
import com.project.ecommerce.service.*;
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

    private final WishlistService wishlistService;

    private final OrderService orderService;

    @GetMapping("/cart")
    public ResponseEntity<APISuccessResponse<ResponseGetCartItemsDto>> getCartItems(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        return new ResponseEntity<>(APISuccessResponse.<ResponseGetCartItemsDto>builder().data(cartService.getCartDetails(user)).build(), HttpStatus.OK);
    }

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

    @PatchMapping("/user-details")
    public ResponseEntity<APISuccessResponse<?>> updateUserDetails(@RequestBody UpdateUserDetailsDto updateUserDetailsDto, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) throws GenericException{
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        userDetailsService.updateUserDetails(updateUserDetailsDto, user);
        return new ResponseEntity<>(APISuccessResponse.builder().data(null).build(), HttpStatus.OK);
    }

    @GetMapping("/wishlist")
    public ResponseEntity<APISuccessResponse<?>> getUserWishlistProducts(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        List<WishListItemDto> wishlist = wishlistService.getAllWishlistItems(user);
        return new ResponseEntity<>(APISuccessResponse.<List<WishListItemDto>>builder().data(wishlist).build(), HttpStatus.OK);
    }

    @PatchMapping("/wishlist")
    public ResponseEntity<APISuccessResponse<?>> updateUserWishlistProducts(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody RequestAddToWishlistDto requestAddToWishlistDto) throws GenericException{
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        wishlistService.addToWishlist(user, requestAddToWishlistDto.getVariantId());
        return new ResponseEntity<>(APISuccessResponse.builder().data(null).build(), HttpStatus.OK);
    }

    @DeleteMapping("/wishlist/{variant_id}")
    public ResponseEntity<APISuccessResponse<?>> removeProductFromWishlist(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable("variant_id") Long variant_id) throws GenericException{
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        wishlistService.removeFromWishlist(user, variant_id);
        return new ResponseEntity<>(APISuccessResponse.builder().data(null).build(), HttpStatus.OK);
    }

    @GetMapping("/order")
    public ResponseEntity<APISuccessResponse<List<ResponseOrderDto>>> getAllOrders(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        List<ResponseOrderDto> responseOrderDtoList = orderService.getAllOrders(user).stream().map(order -> {
            return ResponseOrderDto.builder()
                    .id(order.getId())
                    .orderNumber(order.getOrderNumber())
                    .status(order.getStatus())
                    .paymentMethod(order.getPaymentMethod())
                    .paymentStatus(order.getPaymentStatus())
                    .paymentTransactionId(order.getPaymentTransactionId())
                    .subtotal(order.getSubtotal())
                    .discountedPrice(order.getDiscountedPrice())
                    .shippingCharge(order.getShippingCharge())
                    .shippingAddress(order.getShippingAddress())
                    .billingAddress(order.getBillingAddress())
                    .createdAt(order.getCreatedAt())
                    .paidAt(order.getPaidAt())
                    .shippedAt(order.getShippedAt())
                    .deliveredAt(order.getDeliveredAt())
                    .tax(order.getTax())
                    .totalAmount(order.getTotalAmount())
                    .build();
        }).toList();
        return new ResponseEntity<>(APISuccessResponse.<List<ResponseOrderDto>>builder().data(responseOrderDtoList).build(), HttpStatus.OK);
    }

    @PostMapping("/order")
    public ResponseEntity<APISuccessResponse<?>> placeAnOrder(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody PlaceOrderDto placeOrderDto) {
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        orderService.placeOrder(placeOrderDto, user);
        return new ResponseEntity<>(APISuccessResponse.builder().build(), HttpStatus.OK);
    }

    @GetMapping("/order/{order_id}")
    public ResponseEntity<APISuccessResponse<ResponseOrderDto>> getOrderDetailById(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable("order_id") Long id) throws GenericException{
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        Order order = orderService.getOrderDetails(id);
        if (order == null){
            throw new GenericException("Invalid order id");
        } else if(user != null && user.getId() != order.getUser().getId()){
            throw new GenericException("You can't access this information");
        }
        ResponseOrderDto responseOrderDto = ResponseOrderDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .paymentTransactionId(order.getPaymentTransactionId())
                .subtotal(order.getSubtotal())
                .discountedPrice(order.getDiscountedPrice())
                .shippingCharge(order.getShippingCharge())
                .shippingAddress(order.getShippingAddress())
                .billingAddress(order.getBillingAddress())
                .createdAt(order.getCreatedAt())
                .paidAt(order.getPaidAt())
                .shippedAt(order.getShippedAt())
                .deliveredAt(order.getDeliveredAt())
                .tax(order.getTax())
                .totalAmount(order.getTotalAmount())
                .build();
        return new ResponseEntity<>(APISuccessResponse.<ResponseOrderDto>builder().data(responseOrderDto).build(), HttpStatus.OK);
    }

    @PostMapping("/single-order")
    public ResponseEntity<APISuccessResponse<?>> placeAnSingleItemOrder(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody PlaceSingleOrderDto placeSingleOrderDto) throws GenericException {
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        orderService.placeSingleItemOrder(placeSingleOrderDto, user);
        return new ResponseEntity<>(APISuccessResponse.builder().build(), HttpStatus.OK);
    }
}

