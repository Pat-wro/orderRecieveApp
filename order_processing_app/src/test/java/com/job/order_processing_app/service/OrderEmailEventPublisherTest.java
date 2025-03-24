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

import static com.example.library.infra.MessageReadConst.Topics.EMAIL_TOPIC;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderEmailEventPublisherTest {

    @Mock
    private KafkaTemplate<String, OrderRequestDTO> kafkaTemplate;

    @InjectMocks
    private OrderEmailEventPublisher emailEventPublisher;

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
    void sendEmailEvent_shouldSendMessageToKafka() {
        doReturn(null).when(kafkaTemplate).send(anyString(), any(OrderRequestDTO.class));

        emailEventPublisher.sendEmailEvent(orderRequestDTO);

        verify(kafkaTemplate, times(1)).send(eq(EMAIL_TOPIC), eq(orderRequestDTO));
    }

    @Test
    void sendEmailEvent_withNullOrderRequest_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                emailEventPublisher.sendEmailEvent(null));

        verify(kafkaTemplate, never()).send(anyString(), any());
    }

}