package com.job.order_processing_app.service;


import com.example.library.OrderRequestDTO;
import com.job.order_processing_app.model.OrderRequest;
import com.job.order_processing_app.repository.OrderRepository;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;

    @CircuitBreaker(name = "databaseService", fallbackMethod = "fallbackProcessOrder")
    @Retry(name = "databaseService")
    @Transactional
    public void processOrder(OrderRequestDTO orderRequestDto) {
        Optional<OrderRequest> existingOrder = orderRepository
                .findByShipmentNumber(orderRequestDto.getShipmentNumber());

        OrderRequest order;
        if (existingOrder.isPresent()) {
            order = existingOrder.get();
            order.setReceiverEmail(orderRequestDto.getReceiverEmail());
            order.setReceiverCountryCode(orderRequestDto.getReceiverCountryCode());
            order.setSenderCountryCode(orderRequestDto.getSenderCountryCode());
            order.setStatusCode(orderRequestDto.getStatusCode());
        } else {
//            TODO pomyslec nad converterem
            order = OrderRequest.builder()
                    .shipmentNumber(orderRequestDto.getShipmentNumber())
                    .receiverEmail(orderRequestDto.getReceiverEmail())
                    .receiverCountryCode(orderRequestDto.getReceiverCountryCode())
                    .senderCountryCode(orderRequestDto.getSenderCountryCode())
                    .statusCode(orderRequestDto.getStatusCode())
                    .build();
        }
        log.info("dzialaja logi");
        System.out.println("dupa");
        orderRepository.save(order);
    }
    private void fallbackProcessOrder(OrderRequestDTO orderRequestDto, Exception ex) {
        log.error("Circuit breaker: DB unavailable for shipment: {}",
                orderRequestDto.getShipmentNumber());
    }
}