package com.project.ecommerce.scheduler;

import com.project.ecommerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderExpiryScheduler {

    private final OrderRepository orderRepository;

    @Scheduled(fixedRate = 60000) // every 1 min
    @Transactional
    public void cancelExpiredOrders() {

        LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(5);

        int cancelledCount = orderRepository.cancelExpiredOrders(expiryTime);

        if (cancelledCount > 0) {
            log.info("Auto-cancelled {} expired orders", cancelledCount);
        }
    }
}
