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
        OrderRequest order = orderRepository
                .findByShipmentNumber(orderRequestDto.getShipmentNumber())
                .map(existingOrder -> updateExistingOrder(existingOrder, orderRequestDto))
                .orElseGet(() -> modelMapper.map(orderRequestDto, OrderRequest.class));

        orderRepository.save(order);
    }

    private OrderRequest updateExistingOrder(OrderRequest order, OrderRequestDTO dto) {
        order.setReceiverEmail(dto.getReceiverEmail());
        order.setReceiverCountryCode(dto.getReceiverCountryCode());
        order.setSenderCountryCode(dto.getSenderCountryCode());
        order.setStatusCode(dto.getStatusCode());
        return order;
    }
    private void fallbackProcessOrder(OrderRequestDTO orderRequestDto, Exception ex) {
        log.error("Circuit breaker: DB unavailable for shipment: {}",
                orderRequestDto.getShipmentNumber());
    }
}