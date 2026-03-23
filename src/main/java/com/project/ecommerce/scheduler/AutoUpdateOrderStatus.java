package com.project.ecommerce.scheduler;

import com.project.ecommerce.entity.Order;
import com.project.ecommerce.enums.OrderStatus;
import com.project.ecommerce.repository.OrderRepository;
import com.project.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AutoUpdateOrderStatus {

    private final OrderRepository orderRepository;

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void updateOrderStatuses() {
        LocalDateTime now = LocalDateTime.now();

        List<Order> orders = orderRepository.findOrdersReadyForNextStep(
                now.minusMinutes(2),
                now.minusMinutes(2),
                now.minusMinutes(2));

        log.info("Updating {} orders", orders.size());

        for (Order order : orders) {
            OrderStatus status = order.getStatus();

            switch (status) {

                case PAID:
                    if (order.getPaidAt().plusMinutes(2).isBefore(now)) {
                        order.setStatus(OrderStatus.SHIPPED);
                        order.setShippedAt(now);
                    }
                    break;

                case SHIPPED:
                    if (order.getShippedAt().plusMinutes(2).isBefore(now)) {
                        order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
                        order.setOutForDeliveryAt(now);
                    }
                    break;

                case OUT_FOR_DELIVERY:
                    if (order.getOutForDeliveryAt().plusMinutes(2).isBefore(now)) {
                        order.setStatus(OrderStatus.DELIVERED);
                        order.setDeliveredAt(now);
                    }
                    break;
            }
        }
        orderRepository.saveAll(orders);
    }
}
