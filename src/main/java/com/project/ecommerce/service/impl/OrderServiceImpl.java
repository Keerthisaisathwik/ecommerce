package com.project.ecommerce.service.impl;

import com.project.ecommerce.dto.CartItemDto;
import com.project.ecommerce.dto.PlaceOrderDto;
import com.project.ecommerce.dto.PlaceSingleOrderDto;
import com.project.ecommerce.dto.ResponseGetCartItemsDto;
import com.project.ecommerce.entity.*;
import com.project.ecommerce.enums.OrderStatus;
import com.project.ecommerce.enums.PaymentStatus;
import com.project.ecommerce.exception.GenericException;
import com.project.ecommerce.repository.*;
import com.project.ecommerce.service.CartService;
import com.project.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Override
    public List<Order> getAllOrders(User user) {
        return orderRepository.findByUser(user).orElse(Collections.emptyList());
    }

    @Override
    public Order getOrderDetails(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    @Transactional
    @Override
    public void placeOrder(PlaceOrderDto placeOrderDto, User user){

        ResponseGetCartItemsDto cart = cartService.getCartDetails(user);

        // 1️⃣ Create and save Order FIRST
        Order order = Order.builder()
                .user(user)
                .orderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8))
                .status(OrderStatus.DELIVERED)
                .paymentMethod(placeOrderDto.getPaymentMethod())
                .paymentTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 10))
                .paymentStatus(PaymentStatus.SUCCESS)
                .subtotal(cart.getSubTotal())
                .discountedPrice(cart.getDiscountedPrice())
                .shippingCharge(cart.getShippingCharge())
                .tax(cart.getTax())
                .totalAmount(cart.getTotalAmount())
                .shippingAddress(placeOrderDto.getShippingAddress())
                .billingAddress(placeOrderDto.getBillingAddress())
                .build();

        orderRepository.save(order);

        // 2️⃣ Collect variant ASINs
        List<String> variantAsins = cart.getListOfCartItems()
                .stream()
                .filter(cartItemDto -> !cartItemDto.isSaveForLater())
                .map(CartItemDto::getVariantAsin)
                .toList();

        // 3️⃣ Fetch variants in ONE query
        List<ProductVariant> variants = productVariantRepository.findAllByVariantAsinIn(variantAsins);

        Map<String, ProductVariant> variantMap = new HashMap<>();

        for (ProductVariant v : variants) {
            variantMap.put(v.getVariantAsin(), v);
        }

        // 4️⃣ Create OrderItems
        List<OrderItem> orderItems = cart.getListOfCartItems().stream()
                .filter(cartItemDto -> !cartItemDto.isSaveForLater())
                .map(item -> {
                    ProductVariant variant = variantMap.get(item.getVariantAsin());

                    if (variant == null) {
                        throw new RuntimeException("Variant Asin of "+ item.getVariantAsin() +" is missing");
                    }
                    Double tax = variant.getDiscountedPrice() * (variant.getTaxPercentage() / 100);
                    return OrderItem.builder()
                            .order(order)
                            .user(user)
                            .price(variant.getDiscountedPrice())
                            .tax(tax)
                            .quantity(item.getQuantity())
                            .productName(variant.getProduct().getName())
                            .productVariantId(variant.getId())
                            .totalPrice(variant.getDiscountedPrice() + tax)
                            .build();
                })
                .toList();

        // 5️⃣ Save all order items in one go
        orderItemRepository.saveAll(orderItems);
        cartItemRepository.deleteByCartIdAndVariantAsins(user.getCart().getId(), variantAsins);
    }

    @Override
    public Boolean isPreviouslyOrdered(Long id, User user) {
        return orderItemRepository.existsByProductVariantIdAndUser(id, user);
    }

    @Transactional
    @Override
    public void placeSingleItemOrder(PlaceSingleOrderDto placeSingleOrderDto, User user) throws GenericException{
        ProductVariant productVariant = productVariantRepository.findByVariantAsin(placeSingleOrderDto.getVariantAsin()).orElseThrow(() -> new GenericException("Invalid product variant Asin: "+ placeSingleOrderDto.getVariantAsin()));
        Double tax = (double) Math.round(productVariant.getDiscountedPrice() * productVariant.getTaxPercentage())/100;
        Double shippingCharge = productVariant.getDiscountedPrice() >= 500 ? 0 : 40.0;
        Order order = Order.builder()
                .user(user)
                .orderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8))
                .status(OrderStatus.DELIVERED)
                .paymentMethod(placeSingleOrderDto.getPaymentMethod())
                .paymentTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 10))
                .paymentStatus(PaymentStatus.SUCCESS)
                .subtotal(productVariant.getPrice())
                .discountedPrice(productVariant.getDiscountedPrice())
                .shippingCharge(shippingCharge)
                .tax(tax)
                .totalAmount(productVariant.getDiscountedPrice() + tax + shippingCharge)
                .shippingAddress(placeSingleOrderDto.getShippingAddress())
                .billingAddress(placeSingleOrderDto.getBillingAddress())
                .build();
        orderRepository.save(order);

        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .user(user)
                .productVariantId(productVariant.getId())
                .productName(productVariant.getProduct().getName())
                .price(order.getTotalAmount() - order.getTax())
                .tax(order.getTax())
                .quantity(1)
                .totalPrice(order.getTotalAmount())
                .build();
        orderItemRepository.save(orderItem);
    }
}
