package com.job.order_processing_app.service;

import com.example.library.OrderRequestDTO;
import com.example.library.enums.StatusCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static com.example.library.infra.MessageReadConst.Topics.ORDER_TOPIC;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderMessageServiceTest {

    @Mock
    private KafkaTemplate<String, OrderRequestDTO> kafkaTemplate;

    @InjectMocks
    private OrderMessageService orderMessageService;

    private OrderRequestDTO orderRequestDTO;

    @BeforeEach
    void setUp() {
        orderRequestDTO = OrderRequestDTO.builder()
                .shipmentNumber("SHIP123")
                .receiverEmail("test@example.com")
                .receiverCountryCode("PL")
                .senderCountryCode("UK")
                .statusCode(StatusCode.PROCESSING)
                .build();
    }

    @Test
    void processOrder_shouldSendMessageToKafka() {
        orderMessageService.processOrder(orderRequestDTO);
        verify(kafkaTemplate, times(1)).send(eq(ORDER_TOPIC), eq(orderRequestDTO));
    }
}