package com.job.order_processing_app.service;


import com.example.library.OrderRequestDTO;
import com.job.order_processing_app.model.OrderRequest;
import com.job.order_processing_app.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

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
            order = OrderRequest.builder()
                    .shipmentNumber(orderRequestDto.getShipmentNumber())
                    .receiverEmail(orderRequestDto.getReceiverEmail())
                    .receiverCountryCode(orderRequestDto.getReceiverCountryCode())
                    .senderCountryCode(orderRequestDto.getSenderCountryCode())
                    .statusCode(orderRequestDto.getStatusCode())
                    .build();
        }
        orderRepository.save(order);
    }
}