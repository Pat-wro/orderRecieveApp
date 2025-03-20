package com.job.order_processing_app.service;

import com.example.library.OrderRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.example.library.infra.MessageReadConst.Topics.EMAIL_TOPIC;

@Service
@RequiredArgsConstructor
public class OrderEmailEventPublisher {

    private final KafkaTemplate<String, OrderRequestDTO> kafkaTemplate;

    public void sendEmailEvent(OrderRequestDTO orderRequestDto) {
        if (orderRequestDto == null) {
            throw new IllegalArgumentException("Order request is null");
        }
        kafkaTemplate.send(EMAIL_TOPIC, orderRequestDto);
    }
}
