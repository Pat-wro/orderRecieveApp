package com.job.order_api_app.service;


import com.example.library.OrderRequestDTO;
import com.job.order_api_app.model.OrderRequest;
import com.job.order_api_app.repository.OrderRepository;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    @CircuitBreaker(name = "databaseService", fallbackMethod = "fallbackProcessOrder")
    @Retry(name = "databaseService")
    @Transactional
    public void processOrder(OrderRequestDTO orderRequestDto) {
        Optional<OrderRequest> existingOrder = orderRepository
                .findByShipmentNumber(orderRequestDto.getShipmentNumber());

        if (existingOrder.isPresent()) {
            OrderRequest order = existingOrder.get();
            order.setReceiverEmail(orderRequestDto.getReceiverEmail());
            order.setReceiverCountryCode(orderRequestDto.getReceiverCountryCode());
            order.setSenderCountryCode(orderRequestDto.getSenderCountryCode());
            order.setStatusCode(orderRequestDto.getStatusCode());
            orderRepository.save(order);
        } else {
            OrderRequest newOrder = modelMapper.map(orderRequestDto, OrderRequest.class);
            orderRepository.save(newOrder);
        }
    }
    private void fallbackProcessOrder(OrderRequestDTO orderRequestDto, Exception ex) {
        log.error("Circuit breaker: DB unavailable for shipment: {}",
                orderRequestDto.getShipmentNumber());
    }
}