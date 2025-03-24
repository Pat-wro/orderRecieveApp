package com.job.order_processing_app.service;

import com.example.library.OrderRequestDTO;
import com.example.library.enums.StatusCode;
import com.job.order_processing_app.exception.OrderProcessingException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderListenerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private OrderEmailEventPublisher emailServices;

    @InjectMocks
    private OrderListener orderListener;

    private OrderRequestDTO orderRequestDTO;
    private ConsumerRecord<String, OrderRequestDTO> consumerRecord;

    @BeforeEach
    void setUp() {
        orderRequestDTO = OrderRequestDTO.builder()
                .shipmentNumber("SHIP123")
                .receiverEmail("test@example.com")
                .receiverCountryCode("PL")
                .senderCountryCode("UK")
                .statusCode(StatusCode.PROCESSING)
                .build();

        consumerRecord = new ConsumerRecord<>("order-topic", 0, 0, "key", orderRequestDTO);
    }

    @Test
    void onMessage_shouldProcessOrderAndSendEmail() {
        doNothing().when(orderService).processOrder(any(OrderRequestDTO.class));
        doNothing().when(emailServices).sendEmailEvent(any(OrderRequestDTO.class));

        orderListener.onMessage(consumerRecord);

        verify(orderService, times(1)).processOrder(orderRequestDTO);
        verify(emailServices, times(1)).sendEmailEvent(orderRequestDTO);
    }

    @Test
    void onMessage_withNullOrderRequest_shouldThrowException() {
        ConsumerRecord<String, OrderRequestDTO> recordWithNullValue =
                new ConsumerRecord<>("order-topic", 0, 0, "key", null);

        assertThrows(OrderProcessingException.class, () ->
                orderListener.onMessage(recordWithNullValue));

        verify(orderService, never()).processOrder(any());
        verify(emailServices, never()).sendEmailEvent(any());
    }

    @Test
    void onMessage_whenOrderServiceThrowsException_shouldNotProceedToEmailService() {
        doThrow(new RuntimeException("Database error"))
                .when(orderService).processOrder(any(OrderRequestDTO.class));

        assertThrows(RuntimeException.class, () ->
                orderListener.onMessage(consumerRecord));

        verify(orderService, times(1)).processOrder(orderRequestDTO);
        verify(emailServices, never()).sendEmailEvent(any());
    }
}