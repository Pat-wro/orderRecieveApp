package com.job.order_processing_app.service;

import com.example.library.OrderRequestDTO;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import static com.example.library.infra.MessageReadConst.Topics.EMAIL_TOPIC;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderEmailEventPublisher {

    private final KafkaTemplate<String, OrderRequestDTO> kafkaTemplate;

    @CircuitBreaker(name = "emailService", fallbackMethod = "fallbackSendEmailEvent")
    @Retry(name = "emailService")
    public void sendEmailEvent(OrderRequestDTO orderRequestDto) {
        if (orderRequestDto == null) {
            throw new IllegalArgumentException("Order request is null");
        }
        kafkaTemplate.send(EMAIL_TOPIC, orderRequestDto);
    }
    private void fallbackSendEmailEvent(OrderRequestDTO orderRequestDto, Exception ex) {
        log.error("Circuit breaker: Email service unavailable for shipment: {}",
                orderRequestDto.getShipmentNumber());
    }
}
