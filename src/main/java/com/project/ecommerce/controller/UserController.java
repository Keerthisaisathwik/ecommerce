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

import java.math.RoundingMode;
import java.time.ZoneOffset;
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

    private final UserDetailsService userDetailsService;

    private final WishlistService wishlistService;

    private final OrderService orderService;

    private final UserAddressesService userAddressesService;

    private final InvoiceService invoiceService;

    private final ReviewService reviewService;

    private final ProductVariantService productVariantService;

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
        List<WishListItemDto> wishlist = wishlistService.getAllWishlistItems(user).stream()
                .map((item) ->
                {
                    ProductVariant productVariant = item.getProductVariant();
                    return WishListItemDto.builder()
                            .id(item.getId())
                            .variantAsin(productVariant.getVariantAsin())
                            .name(productVariant.getProduct().getName())
                            .description(productVariant.getProduct().getDescription())
                            .price(productVariant.getPrice())
                            .imageUrl(productVariant.getImageUrls().getFirst())
                            .isAvailable(productVariant.getIsAvailable())
                            .discountedPrice(productVariant.getDiscountedPrice())
                            .addedAt(item.getAddedAt())
                            .build();
                }).toList();
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
                    .price(order.getPrice())
                    .shippingCharge(order.getShippingCharge())
                    .deliveryAddress(order.getDeliveryAddress())
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
    public ResponseEntity<APISuccessResponse<OrderPlacedResponseDto>> placeAnOrder(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody PlaceOrderDto placeOrderDto) throws GenericException{
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        String paymentToken = orderService.placeOrder(placeOrderDto, user);
        return new ResponseEntity<>(APISuccessResponse.<OrderPlacedResponseDto>builder().data(OrderPlacedResponseDto.builder().paymentToken(paymentToken).build()).build(), HttpStatus.OK);
    }

    @GetMapping("/order/{order_id}/summary")
    public ResponseEntity<APISuccessResponse<OrderSummaryDto>> getOrderSummaryByOrderNumber(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable("order_id") String id) throws GenericException{
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        Order order = orderService.getOrderByOrderNumber(user, id);
        if (order == null){
            throw new GenericException("Invalid order id");
        } else if(user != null && user.getId() != order.getUser().getId()){
            throw new GenericException("You can't access this information");
        }
        OrderSummaryDto orderSummaryDto = OrderSummaryDto.builder()
                .paymentMethod(order.getPaymentMethod())
                .orderId(order.getOrderNumber())
                .price(order.getPrice().setScale(2, RoundingMode.HALF_UP))
                .build();
        return new ResponseEntity<>(APISuccessResponse.<OrderSummaryDto>builder().data(orderSummaryDto).build(), HttpStatus.OK);
    }

    @GetMapping("/order/{order_id}")
    public ResponseEntity<APISuccessResponse<ResponseOrderDetailsDto>> getOrderDetailByOrderNumber(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable("order_id") String id) throws GenericException{
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        Order order = orderService.getOrderByOrderNumber(user, id);
        if (order == null){
            throw new GenericException("Invalid order id");
        } else if(user != null && user.getId() != order.getUser().getId()){
            throw new GenericException("You can't access this information");
        }
        List<OrderItemDto> orderItemDtoList = orderService.getOrderItemsOfOrder(order).stream().map(orderItem -> {
            try {
                ProductVariant productVariant = productVariantService.findByVariantId(orderItem.getProductVariantId());
                return OrderItemDto.builder()
                        .variantAsin(productVariant.getVariantAsin())
                        .quantity(orderItem.getQuantity())
                        .name(productVariant.getProduct().getName())
                        .description(productVariant.getProduct().getDescription())
                        .price(orderItem.getPricePerUnit())
                        .imageUrl(productVariant.getImageUrls().isEmpty() ? "" : productVariant.getImageUrls().getFirst())
                        .totalAmount(orderItem.getTotalAmount())
                        .build();
            } catch (GenericException e) {
                throw new RuntimeException(e);
            }
        }).toList();
        ResponseOrderDetailsDto responseOrderDetailsDto = ResponseOrderDetailsDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .paymentTransactionId(order.getPaymentTransactionId())
                .price(order.getPrice())
                .shippingCharge(order.getShippingCharge())
                .deliveryAddress(order.getDeliveryAddress())
                .billingAddress(order.getBillingAddress())
                .createdAt(order.getCreatedAt())
                .paidAt(order.getPaidAt())
                .shippedAt(order.getShippedAt())
                .deliveredAt(order.getDeliveredAt())
                .tax(order.getTax())
                .totalAmount(order.getTotalAmount())
                .orderItemsList(orderItemDtoList)
                .build();
        return new ResponseEntity<>(APISuccessResponse.<ResponseOrderDetailsDto>builder().data(responseOrderDetailsDto).build(), HttpStatus.OK);
    }

    @PostMapping("/single-order")
    public ResponseEntity<APISuccessResponse<OrderPlacedResponseDto>> placeAnSingleItemOrder(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody PlaceSingleOrderDto placeSingleOrderDto) throws GenericException {
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        String paymentToken = orderService.placeSingleItemOrder(placeSingleOrderDto, user);
        return new ResponseEntity<>(APISuccessResponse.<OrderPlacedResponseDto>builder().data(OrderPlacedResponseDto.builder().paymentToken(paymentToken).build()).build(), HttpStatus.OK);
    }

    @GetMapping("/order/{order-id}/invoice")
    public ResponseEntity<byte[]> downloadInvoice(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable("order-id") String orderId) throws Exception {
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        Order order = orderService.getOrderByOrderNumber(user, orderId);
        byte[] pdf = invoiceService.generateInvoice(order);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=invoice-" + order.getOrderNumber() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(pdf);
    }

    @PostMapping("/{payment_token}/pay")
    public ResponseEntity<APISuccessResponse<PaymentSuccessfulDto>> pay(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable("payment_token") String paymentToken) throws Exception{
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        Order order = orderService.payTheOder(user, paymentToken);
        PaymentSuccessfulDto paymentSuccessfulDto = PaymentSuccessfulDto.builder().orderId(order.getOrderNumber()).build();
        return new ResponseEntity<>(APISuccessResponse.<PaymentSuccessfulDto>builder().data(paymentSuccessfulDto).build(), HttpStatus.OK);
    }

    @PostMapping("/{payment_token}/cancel-payment")
    public ResponseEntity<APISuccessResponse<?>> cancelPayment(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable("payment_token") String paymentToken) throws GenericException{
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        orderService.cancelTheOrder(user, paymentToken);
        return new ResponseEntity<>(APISuccessResponse.builder().data(null).build(), HttpStatus.OK);
    }

    @GetMapping("/get-payment-details/{payment-token}")
    public ResponseEntity<APISuccessResponse<PaymentDetailsDto>> getPaymentDetails(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable("payment-token") String paymentToken) throws GenericException{
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        Order order = orderService.getPaymentDetails(user, paymentToken);
        PaymentDetailsDto paymentDetailsDto = PaymentDetailsDto.builder()
                .deliveryAddress(order.getDeliveryAddress())
                .paymentMethod(order.getPaymentMethod())
                .price(order.getPrice())
                .paymentExpiresAt(order.getPaymentExpiryTime())
                .build();
        return new ResponseEntity<>(APISuccessResponse.<PaymentDetailsDto>builder().data(paymentDetailsDto).build(), HttpStatus.OK);
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

    @GetMapping("/review/{variant_asin}")
    public ResponseEntity<APISuccessResponse<ProductReviewDto>> getReviewDetailsIfPresent(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable("variant_asin") String variantAsin) throws GenericException{
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        Review review = reviewService.getReview(variantAsin, user);
        ProductReviewDto productReviewDto = new ProductReviewDto();
        if(review == null){
            productReviewDto = null;
        }else{
            productReviewDto = ProductReviewDto.builder()
                    .name(user.getUserDetails().getFirstName()+" "+user.getUserDetails().getLastName())
                    .rating(review.getRating())
                    .reviewMessage(review.getComment())
                    .imageUrls(review.getImageUrls())
                    .variantAttributeList(review.getProductVariant().getAttributes().stream().map(variantAttribute -> VariantAttributeDto.builder()
                                    .attributeName(variantAttribute.getAttributeName())
                                    .attributeValue(variantAttribute.getAttributeValue())
                                    .build())
                            .toList())
                    .updatedAt(review.getUpdatedAt())
                    .build();
        }
        return new ResponseEntity<>(APISuccessResponse.<ProductReviewDto>builder().data(productReviewDto).build(), HttpStatus.OK);
    }


    @PostMapping("/review")
    public ResponseEntity<APISuccessResponse<ProductReviewDto>> addOrUpdateReview(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody UpdateReviewDto updateReviewDto) throws GenericException{
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        Review review = reviewService.updateReview(updateReviewDto, user);
        ProductReviewDto productReviewDto = ProductReviewDto.builder()
                .name(user.getUserDetails().getFirstName()+" "+user.getUserDetails().getLastName())
                .rating(review.getRating())
                .reviewMessage(review.getComment())
                .imageUrls(review.getImageUrls())
                .variantAttributeList(review.getProductVariant().getAttributes().stream().map(variantAttribute -> VariantAttributeDto.builder()
                                .attributeName(variantAttribute.getAttributeName())
                                .attributeValue(variantAttribute.getAttributeValue())
                                .build())
                                .toList())
                .updatedAt(review.getUpdatedAt())
                .build();
        return new ResponseEntity<>(APISuccessResponse.<ProductReviewDto>builder().data(productReviewDto).build(),
                HttpStatus.OK);
    }

    @DeleteMapping("/review/{variant_asin}")
    public ResponseEntity<APISuccessResponse<?>> deleteReview(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable("variant_asin") String variantAsin) throws GenericException{
        String token = authorizationHeader.substring(7);
        User user = userService.findUserByToken(token);
        reviewService.deleteReview(variantAsin, user);
        return new ResponseEntity<>(APISuccessResponse.builder().data(null).build(), HttpStatus.OK);
    }
}

