package com.job.order_api_app.service;

import com.example.library.OrderRequestDTO;
import com.example.library.enums.StatusCode;
import com.job.order_api_app.model.OrderRequest;
import com.job.order_api_app.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OrderService orderService;

    @Captor
    private ArgumentCaptor<OrderRequest> orderRequestCaptor;

    private OrderRequestDTO orderRequestDTO;
    private OrderRequest existingOrder;
    private UUID existingOrderId;

    @BeforeEach
    void setUp() {
        orderRequestDTO = OrderRequestDTO.builder()
                .shipmentNumber("SHIP123")
                .receiverEmail("test@example.com")
                .receiverCountryCode("PL")
                .senderCountryCode("UK")
                .statusCode(StatusCode.PROCESSING)
                .build();

        existingOrderId = UUID.randomUUID();
        existingOrder = OrderRequest.builder()
                .orderId(existingOrderId)
                .shipmentNumber("SHIP123")
                .receiverEmail("old@example.com")
                .receiverCountryCode("US")
                .senderCountryCode("DE")
                .statusCode(StatusCode.NEW)
                .build();
    }

    @Test
    void processOrder_withNewOrder_shouldSaveNewOrder() {
        when(orderRepository.findByShipmentNumber(anyString())).thenReturn(Optional.empty());

        when(modelMapper.map(eq(orderRequestDTO), eq(OrderRequest.class))).thenAnswer(invocation -> {
            return OrderRequest.builder()
                    .shipmentNumber(orderRequestDTO.getShipmentNumber())
                    .receiverEmail(orderRequestDTO.getReceiverEmail())
                    .receiverCountryCode(orderRequestDTO.getReceiverCountryCode())
                    .senderCountryCode(orderRequestDTO.getSenderCountryCode())
                    .statusCode(orderRequestDTO.getStatusCode())
                    .build();
        });

        orderService.processOrder(orderRequestDTO);

        verify(orderRepository).save(orderRequestCaptor.capture());
        OrderRequest savedOrder = orderRequestCaptor.getValue();

        assertEquals(orderRequestDTO.getShipmentNumber(), savedOrder.getShipmentNumber());
        assertEquals(orderRequestDTO.getReceiverEmail(), savedOrder.getReceiverEmail());
        assertEquals(orderRequestDTO.getReceiverCountryCode(), savedOrder.getReceiverCountryCode());
        assertEquals(orderRequestDTO.getSenderCountryCode(), savedOrder.getSenderCountryCode());
        assertEquals(orderRequestDTO.getStatusCode(), savedOrder.getStatusCode());
    }

    @Test
    void processOrder_withExistingOrder_shouldUpdateOrder() {
        when(orderRepository.findByShipmentNumber(orderRequestDTO.getShipmentNumber())).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(any(OrderRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderService.processOrder(orderRequestDTO);

        verify(orderRepository).save(orderRequestCaptor.capture());
        OrderRequest savedOrder = orderRequestCaptor.getValue();

        assertEquals(existingOrderId, savedOrder.getOrderId());

        assertEquals(orderRequestDTO.getReceiverEmail(), savedOrder.getReceiverEmail());
        assertEquals(orderRequestDTO.getReceiverCountryCode(), savedOrder.getReceiverCountryCode());
        assertEquals(orderRequestDTO.getSenderCountryCode(), savedOrder.getSenderCountryCode());
        assertEquals(orderRequestDTO.getStatusCode(), savedOrder.getStatusCode());
    }

    @Test
    void processOrder_whenRepositoryThrowsException_shouldNotCatchException() {

        when(orderRepository.findByShipmentNumber(anyString())).thenThrow(new RuntimeException("Database error"));

        try {
            orderService.processOrder(orderRequestDTO);
        } catch (Exception e) {
        }
        verify(orderRepository, never()).save(any());
    }
}