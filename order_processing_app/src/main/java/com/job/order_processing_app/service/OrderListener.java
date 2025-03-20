package com.job.order_processing_app.service;

import com.example.library.OrderRequestDTO;
import com.job.order_processing_app.exception.OrderProcessingException;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.example.library.infra.MessageReadConst.Groups.MESSAGE_READ_GROUP;
import static com.example.library.infra.MessageReadConst.Listeners.MESSAGE_READ_LISTENER_CONTAINER_FACTORY;
import static com.example.library.infra.MessageReadConst.Topics.ORDER_TOPIC;

@Service
@RequiredArgsConstructor
public class OrderListener {

    private final OrderService orderService;
    private final OrderEmailEventPublisher emailServices;


    @KafkaListener(topics = ORDER_TOPIC, groupId = MESSAGE_READ_GROUP, containerFactory = MESSAGE_READ_LISTENER_CONTAINER_FACTORY)
    public void onMessage(ConsumerRecord<String, OrderRequestDTO> record) {
        OrderRequestDTO orderRequestDto = record.value();
        if (orderRequestDto == null) {
            throw new OrderProcessingException("Received null Order Request");
        }
        orderService.processOrder(orderRequestDto);
        emailServices.sendEmailEvent(orderRequestDto);
    }

}