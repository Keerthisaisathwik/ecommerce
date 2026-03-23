package com.project.ecommerce.service;

import com.project.ecommerce.dto.PlaceOrderDto;
import com.project.ecommerce.dto.PlaceSingleOrderDto;
import com.project.ecommerce.entity.Order;
import com.project.ecommerce.entity.OrderItem;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.enums.OrderStatus;
import com.project.ecommerce.exception.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    public Page<Order> getAllOrders(User user, Pageable pageable);

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

    public void updateOrderStatusAdmin(Order order) throws GenericException;

    public boolean isValidTransition(OrderStatus current, OrderStatus next);
}
