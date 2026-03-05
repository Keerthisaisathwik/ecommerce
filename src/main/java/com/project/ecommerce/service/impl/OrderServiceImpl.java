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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    @Value("${order.cancellation.minutes}")
    private int cancellationMinutes;

    @Override
    public List<Order> getAllOrders(User user) {
        return orderRepository.findByUser(user).orElse(Collections.emptyList());
    }

    @Override
    public Order getOrderById(User user, Long orderId) throws GenericException{
        Order order = orderRepository.findById(orderId).orElse(null);
        if(order.getUser() != user){
            throw new GenericException("You dont have permission to access users data");
        }
        return order;
    }

    @Transactional
    @Override
    public String placeOrder(PlaceOrderDto placeOrderDto, User user) throws GenericException{

        ResponseGetCartItemsDto cart = cartService.getCartDetails(user);
        BigDecimal totalTax = BigDecimal.ZERO;

        // Step 1: Calculate tax details
        for (CartItemDto item : cart.getListOfCartItems()) {

            if (item.isSaveForLater()) continue;

            ProductVariant variant = productVariantRepository.findByVariantAsin(item.getVariantAsin()).orElseThrow(() -> new GenericException("Invalid productAsin: "+ item.getVariantAsin()));

            BigDecimal taxRate = variant.getTaxPercentage();
            BigDecimal totalPricePerUnit = variant.getDiscountedPrice();
            int quantity = item.getQuantity();

//            BigDecimal basePricePerUnit = totalPricePerUnit / (1 + taxRate / 100);
//            BigDecimal taxPerUnit = totalPricePerUnit - basePricePerUnit;
//            totalTax += taxPerUnit * quantity;

            BigDecimal multiplier = BigDecimal.ONE.add(taxRate.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));
            BigDecimal basePricePerUnit = totalPricePerUnit.divide(multiplier, 6, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
            BigDecimal taxPerUnit = totalPricePerUnit.subtract(basePricePerUnit).setScale(2, RoundingMode.HALF_UP);

            totalTax = totalTax.add(taxPerUnit.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP));

        }

        // Step 2: Create and save Order FIRST
        String paymentToken = UUID.randomUUID().toString().replace("-", "").substring(0,9);
        Order order = Order.builder()
                .user(user)
                .orderNumber("ORD_" + UUID.randomUUID().toString().substring(0, 8))
                .status(OrderStatus.PAYMENT_PENDING)
                .paymentMethod(placeOrderDto.getPaymentMethod())
                .paymentTransactionId("")
                .paymentStatus(PaymentStatus.PENDING)
                .price(cart.getDiscountedPrice())
                .shippingCharge(cart.getShippingCharge())
                .tax(totalTax)
                .totalAmount(cart.getTotalAmount())
                .deliveryAddress(placeOrderDto.getDeliveryAddress())
                .billingAddress(placeOrderDto.getBillingAddress())
                .paymentToken(paymentToken)
                .paymentExpiryTime(Instant.now().plus(cancellationMinutes, ChronoUnit.MINUTES))
                .build();
        orderRepository.save(order);

        // Step 3: Create OrderItems
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItemDto item : cart.getListOfCartItems()) {

            if (item.isSaveForLater()) continue;

            ProductVariant variant = productVariantRepository.findByVariantAsin(item.getVariantAsin()).orElseThrow(() -> new GenericException("Invalid productAsin: "+ item.getVariantAsin()));

            BigDecimal taxRate = variant.getTaxPercentage();
            BigDecimal totalPricePerUnit = variant.getDiscountedPrice();
            int quantity = item.getQuantity();

//            BigDecimal basePricePerUnit = totalPricePerUnit / (1 + taxRate / 100);
//            BigDecimal taxPerUnit = totalPricePerUnit - basePricePerUnit;
            BigDecimal divisor = BigDecimal.ONE.add(taxRate.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));
            BigDecimal basePricePerUnit = totalPricePerUnit.divide(divisor, 6, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
            BigDecimal taxPerUnit = totalPricePerUnit.subtract(basePricePerUnit).setScale(2, RoundingMode.HALF_UP);

            System.out.println("** "+basePricePerUnit + " - "+basePricePerUnit.multiply(BigDecimal.valueOf(quantity))+" - "+basePricePerUnit.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP));
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .user(user)
                    .productVariantId(variant.getId())
                    .productName(variant.getProduct().getName() + "-"+ variant.getFormattedAttributesKey())
                    .pricePerUnit(totalPricePerUnit)
                    .unitPriceWithoutTax(basePricePerUnit)
                    .taxPerUnit(taxPerUnit)
                    .taxRate(taxRate)
                    .quantity(quantity)
                    .netAmount(basePricePerUnit.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP))
                    .totalTaxAmount(taxPerUnit.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP))
                    .totalAmount(totalPricePerUnit.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP))
                    .build();

            orderItems.add(orderItem);
        }

        // Step 4: Save all order items in one go
        orderItemRepository.saveAll(orderItems);
        return paymentToken;
    }

    @Override
    public Boolean isPreviouslyOrdered(Long id, User user, OrderStatus orderStatus) {
        return orderItemRepository.existsByProductVariantIdAndUserAndOrder_Status(id, user, orderStatus);
    }

    @Transactional
    @Override
    public String placeSingleItemOrder(PlaceSingleOrderDto placeSingleOrderDto, User user) throws GenericException{
        ProductVariant productVariant = productVariantRepository.findByVariantAsin(placeSingleOrderDto.getVariantAsin()).orElseThrow(() -> new GenericException("Invalid product variant Asin: "+ placeSingleOrderDto.getVariantAsin()));
        // total price is discounted price, tax rate is already availabe, need to find tax price and base price of the product
//        BigDecimal basePrice = productVariant.getDiscountedPrice() / (1 + productVariant.getTaxPercentage() / 100);
//        BigDecimal taxPrice = productVariant.getDiscountedPrice() - basePrice;
//        BigDecimal subTotal = productVariant.getDiscountedPrice() * placeSingleOrderDto.getQuantity();
//        BigDecimal shippingCharge = productVariant.getDiscountedPrice() >= 500 ? 0 : 40.0;

        BigDecimal taxRate = productVariant.getTaxPercentage();
        BigDecimal totalPricePerUnit = productVariant.getDiscountedPrice();
        int quantity = placeSingleOrderDto.getQuantity();

        BigDecimal multiplier = BigDecimal.ONE.add(taxRate.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));
        BigDecimal basePricePerUnit = totalPricePerUnit.divide(multiplier, 6, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
        BigDecimal taxPerUnit = totalPricePerUnit.subtract(basePricePerUnit).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = totalPricePerUnit.multiply(BigDecimal.valueOf(quantity));
        BigDecimal shippingCharge = totalAmount.compareTo(BigDecimal.valueOf(500)) >= 0 ? BigDecimal.ZERO : BigDecimal.valueOf(40);

        String paymentToken = UUID.randomUUID().toString().replace("-", "").substring(0,9);
        Order order = Order.builder()
                .user(user)
                .orderNumber("ORD_" + UUID.randomUUID().toString().replace("-","").substring(0, 8))
                .status(OrderStatus.PAYMENT_PENDING)
                .paymentMethod(placeSingleOrderDto.getPaymentMethod())
                .paymentTransactionId("")
                .paymentStatus(PaymentStatus.PENDING)
                .price(productVariant.getDiscountedPrice())
                .shippingCharge(shippingCharge)
                .tax(taxPerUnit.multiply(BigDecimal.valueOf(quantity)))
                .totalAmount(totalPricePerUnit.multiply(BigDecimal.valueOf(quantity)))
                .deliveryAddress(placeSingleOrderDto.getDeliveryAddress())
                .billingAddress(placeSingleOrderDto.getBillingAddress())
                .paymentToken(paymentToken)
                .paymentExpiryTime(Instant.now().plus(cancellationMinutes, ChronoUnit.MINUTES))
                .build();
        orderRepository.save(order);

        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .user(user)
                .productVariantId(productVariant.getId())
                .productName(productVariant.getProduct().getName() + "-" + productVariant.getFormattedAttributesKey())
                .pricePerUnit(productVariant.getDiscountedPrice())
                .unitPriceWithoutTax(basePricePerUnit)
                .taxPerUnit(taxPerUnit)
                .taxRate(productVariant.getTaxPercentage())
                .quantity(placeSingleOrderDto.getQuantity())
                .netAmount(basePricePerUnit.multiply(BigDecimal.valueOf(quantity)))
                .totalTaxAmount(taxPerUnit.multiply(BigDecimal.valueOf(quantity)))
                .totalAmount(totalPricePerUnit.multiply(BigDecimal.valueOf(quantity)))
                .build();
        orderItemRepository.save(orderItem);

        return paymentToken;
    }

    @Override
    public Order payTheOder(User user, String paymentToken) throws GenericException {
        Order order = getPaymentDetails(user, paymentToken);
        if(order.getPaymentStatus() != PaymentStatus.PENDING || order.getStatus() != OrderStatus.PAYMENT_PENDING){
            throw new GenericException("Invalid order state");
        }
        order.setPaymentStatus(PaymentStatus.SUCCESS);
        order.setPaymentTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 10));
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
        return order;
    }

    @Override
    public void cancelTheOrder(User user, String paymentToken) throws GenericException {
        Order order = getPaymentDetails(user, paymentToken);
        if(order.getPaymentStatus() != PaymentStatus.PENDING || order.getStatus() != OrderStatus.PAYMENT_PENDING){
            throw new GenericException("Invalid order state");
        }
        order.setPaymentStatus(PaymentStatus.FAILED);
        orderRepository.save(order);
    }

    @Override
    public Order getPaymentDetails(User user, String paymentToken) throws GenericException {
        Order order = orderRepository.findByUserAndPaymentToken(user, paymentToken).orElseThrow(() -> new GenericException("Invalid payment token"));
        if(Instant.now().isAfter(order.getPaymentExpiryTime())){
            throw new GenericException("This Token has expired");
        }
        return order;
    }

    @Override
    public int cancelExpiredOrders() {
        LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(cancellationMinutes);
        return orderRepository.cancelExpiredOrders(expiryTime);
    }

    @Override
    public Order getOrderByOrderNumber(User user, String orderNumber) throws GenericException {
        Order order = orderRepository.findByOrderNumber(orderNumber).orElseThrow(() -> new GenericException("Invalid OrderId"));
        if(order.getUser() != user){
            throw new GenericException("You dont have permission to access users data");
        }
        return order;
    }

    @Override
    public List<OrderItem> getOrderItemsOfOrder(Order order){
        return orderItemRepository.findByOrder(order);
    }
}
