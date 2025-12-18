package com.project.ecommerce.service;

import com.project.ecommerce.dto.PlaceOrderDto;
import com.project.ecommerce.dto.PlaceSingleOrderDto;
import com.project.ecommerce.entity.Order;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.exception.GenericException;

import java.util.List;

public interface OrderService {
    public List<Order> getAllOrders(User user);

    public Order getOrderDetails(Long orderId);

    public void placeOrder(PlaceOrderDto placeOrderDto, User user);

    Boolean isPreviouslyOrdered(Long id, User user);

    public void placeSingleItemOrder(PlaceSingleOrderDto placeSingleOrderDto, User user) throws GenericException;
}
