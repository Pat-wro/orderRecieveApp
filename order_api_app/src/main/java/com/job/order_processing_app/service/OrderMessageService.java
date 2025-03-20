package com.job.order_processing_app.service;


import com.example.library.OrderRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static com.example.library.infra.MessageReadConst.Topics.ORDER_TOPIC;

@Service
@RequiredArgsConstructor
public class OrderMessageService {
    private final KafkaTemplate<String, OrderRequestDTO> kafkaTemplate;

    @Async("asyncTaskExecutor")
    public void processOrder(OrderRequestDTO orderRequest) {
        kafkaTemplate.send(ORDER_TOPIC, orderRequest);
    }
}