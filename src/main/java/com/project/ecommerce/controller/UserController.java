package com.project.ecommerce.controller;

import com.project.ecommerce.dto.*;
import com.project.ecommerce.entity.*;
import com.project.ecommerce.exception.GenericException;
import com.project.ecommerce.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    private final UserAddressesService userAddressesService;

    private final InvoiceService invoiceService;

    @GetMapping("/cart")
    public ResponseEntity<APISuccessResponse<ResponseGetCartItemsDto>> getCartItems(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) throws GenericException{
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        return new ResponseEntity<>(APISuccessResponse.<ResponseGetCartItemsDto>builder().data(cartService.getCartDetails(user)).build(), HttpStatus.OK);
    }

    @PostMapping("/cart")
    public ResponseEntity<APISuccessResponse<?>> updateCartItemsQuantity(@RequestBody UpdateCartItemQuantityDto updateCartItemQuantityDto, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) throws GenericException{
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        cartService.setProductQuantity(user, updateCartItemQuantityDto);
        return new ResponseEntity<>(APISuccessResponse.builder().data(null).build(), HttpStatus.OK);
    }

    @PatchMapping("/save-for-later")
    public ResponseEntity<APISuccessResponse<?>> updateCartItemSaveForLater(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody SaveForLaterDto saveForLaterDto) throws GenericException{
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        cartService.saveForLater(user, saveForLaterDto);
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
        wishlistService.addToWishlist(user, requestAddToWishlistDto.getVariantAsin());
        return new ResponseEntity<>(APISuccessResponse.builder().data(null).build(), HttpStatus.OK);
    }

    @DeleteMapping("/wishlist/{variant_asin}")
    public ResponseEntity<APISuccessResponse<?>> removeProductFromWishlist(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable("variant_asin") String variantAsin) throws GenericException{
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        wishlistService.removeFromWishlist(user, variantAsin);
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
    public ResponseEntity<APISuccessResponse<?>> placeAnOrder(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody PlaceOrderDto placeOrderDto) throws GenericException{
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        orderService.placeOrder(placeOrderDto, user);
        return new ResponseEntity<>(APISuccessResponse.builder().build(), HttpStatus.OK);
    }

    @GetMapping("/order/{order_id}")
    public ResponseEntity<APISuccessResponse<ResponseOrderDto>> getOrderDetailById(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable("order_id") Long id) throws GenericException{
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        Order order = orderService.getOrderById(user, id);
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

    @GetMapping("order/{id}/invoice")
    public ResponseEntity<byte[]> downloadInvoice(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable Long id) throws Exception {
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        Order order = orderService.getOrderById(user, id);
        byte[] pdf = invoiceService.generateInvoice(order);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=invoice-" + order.getOrderNumber() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(pdf);
    }

    @PostMapping("/{order_id}/pay")
    public ResponseEntity<byte[]> pay(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable("order_id") Long id) throws Exception{
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        orderService.payTheOder(user, id);
        Order order = orderService.getOrderById(user, id);
        byte[] pdf = invoiceService.generateInvoice(order);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=invoice-" + order.getOrderNumber() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(pdf);
    }

    @PostMapping("/{order_id}/cancel-payment")
    public ResponseEntity<APISuccessResponse<?>> cancelPayment(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable("order_id") Long id) throws GenericException{
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        orderService.cancelTheOrder(user, id);
        return new ResponseEntity<>(APISuccessResponse.builder().data(null).build(), HttpStatus.OK);
    }

    @GetMapping("/address")
    public ResponseEntity<APISuccessResponse<List<AddressDto>>> getUserAddresses(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        List<UserAddresses> userAddresses = userAddressesService.getAllAddressesOfUser(user);
        List<AddressDto> userAddressesDto = new ArrayList<>();
        for(UserAddresses address : userAddresses){
            userAddressesDto.add(new AddressDto().builder()
                            .id(address.getId())
                            .addressLine1(address.getAddressLine1())
                            .addressLine2(address.getAddressLine2())
                            .addressLine3(address.getAddressLine3())
                            .pincode(address.getPincode())
                            .city(address.getCity())
                            .state(address.getState())
                            .country(address.getCountry())
                            .phoneNumber(address.getPhoneNumber())
                            .alternativePhoneNumber(address.getAlternativePhoneNumber())
                            .name(address.getName())
                    .build());
        }
        return new ResponseEntity<>(APISuccessResponse.<List<AddressDto>>builder().data(userAddressesDto).build(),
                HttpStatus.OK);
    }

    @PostMapping("/address")
    public ResponseEntity<APISuccessResponse<?>> addNewAddress(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody SaveAddressDto saveAddressDto) throws GenericException {
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        userAddressesService.addNewAddress(user, saveAddressDto);
        return new ResponseEntity<>(APISuccessResponse.builder().build(), HttpStatus.OK);
    }

    @PatchMapping("/address")
    public ResponseEntity<APISuccessResponse<?>> updateExistingAddress(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody AddressDto addressDto) throws GenericException{
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        userAddressesService.updateUserAddress(user, addressDto);
        return new ResponseEntity<>(APISuccessResponse.builder().build(), HttpStatus.OK);
    }

    @DeleteMapping("/address/{address_id}")
    public ResponseEntity<APISuccessResponse<?>> deleteUserAddress(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable("address_id") Long address_id) throws GenericException{
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        userAddressesService.deleteUserAddress(user, address_id);
        return new ResponseEntity<>(APISuccessResponse.builder().data(null).build(), HttpStatus.OK);
    }

    @PatchMapping("/cart/save-for-later")
    public ResponseEntity<APISuccessResponse<?>> addToCartAndSaveForLater(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody AddToCartAndSaveForLaterDto addToCartAndSaveForLaterDto) throws GenericException{
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        cartService.addToCartAndSaveForLater(user, addToCartAndSaveForLaterDto);
        return new ResponseEntity<>(APISuccessResponse.builder().data(null).build(), HttpStatus.OK);
    }
}

