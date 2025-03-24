package com.job.order_processing_app.service;


import com.example.library.OrderRequestDTO;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static com.example.library.infra.MessageReadConst.Topics.ORDER_TOPIC;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderMessageService {
    private final KafkaTemplate<String, OrderRequestDTO> kafkaTemplate;

    @CircuitBreaker(name = "kafkaService", fallbackMethod = "fallbackProcessOrder")
    @Retry(name = "kafkaService")
    @Async("asyncTaskExecutor")
    public void processOrder(OrderRequestDTO orderRequest) {
        kafkaTemplate.send(ORDER_TOPIC, orderRequest);
    }

    private void fallbackProcessOrder(OrderRequestDTO orderRequest, Exception ex) {
        log.error("Circuit breaker: Kafka unavailable for shipment: {}",
                orderRequest.getShipmentNumber());
    }
}