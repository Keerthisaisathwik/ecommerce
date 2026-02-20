package com.project.ecommerce.service;

import com.project.ecommerce.dto.PlaceOrderDto;
import com.project.ecommerce.dto.PlaceSingleOrderDto;
import com.project.ecommerce.entity.Order;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.exception.GenericException;

import java.util.List;

public interface OrderService {
    public List<Order> getAllOrders(User user);

    public Order getOrderById(User user, Long orderId) throws GenericException;

    public void placeOrder(PlaceOrderDto placeOrderDto, User user) throws GenericException;

    public Boolean isPreviouslyOrdered(Long id, User user);

    public void placeSingleItemOrder(PlaceSingleOrderDto placeSingleOrderDto, User user) throws GenericException;

    public void payTheOder(User user, Long orderId) throws GenericException;

    public void cancelTheOrder(User user, Long orderId) throws GenericException;
}
