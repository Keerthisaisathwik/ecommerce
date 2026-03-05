package com.project.ecommerce.service;

import com.project.ecommerce.dto.PlaceOrderDto;
import com.project.ecommerce.dto.PlaceSingleOrderDto;
import com.project.ecommerce.entity.Order;
import com.project.ecommerce.entity.OrderItem;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.enums.OrderStatus;
import com.project.ecommerce.exception.GenericException;

import java.util.List;

public interface OrderService {
    public List<Order> getAllOrders(User user);

    public Order getOrderById(User user, Long orderId) throws GenericException;

    public String placeOrder(PlaceOrderDto placeOrderDto, User user) throws GenericException;

    public Boolean isPreviouslyOrdered(Long id, User user, OrderStatus orderStatus);

    public String placeSingleItemOrder(PlaceSingleOrderDto placeSingleOrderDto, User user) throws GenericException;

    public Order payTheOder(User user, String paymentToken) throws GenericException;

    public void cancelTheOrder(User user, String paymentToken) throws GenericException;

    public Order getPaymentDetails(User user, String paymentToken) throws GenericException;

    public int cancelExpiredOrders();

    public Order getOrderByOrderNumber(User user, String orderNumber) throws GenericException;

    public List<OrderItem> getOrderItemsOfOrder(Order order);
}
