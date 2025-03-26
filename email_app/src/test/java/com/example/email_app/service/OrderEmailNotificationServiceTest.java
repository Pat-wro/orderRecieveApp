package com.example.email_app.service;

import com.example.email_app.util.PdfGeneratorUtil;
import com.example.library.OrderRequestDTO;
import com.example.library.enums.StatusCode;
import jakarta.mail.MessagingException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class OrderEmailNotificationServiceTest {

    @InjectMocks
    private OrderEmailNotificationService emailNotificationService;

    @Mock
    private PdfGeneratorUtil pdfGeneratorUtil;

    @Mock
    private EmailSenderService emailSenderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listenOrderRequests_Success() throws MessagingException {
        // Arrange
        OrderRequestDTO orderRequestDTO = createTestOrderRequestDTO();
        ConsumerRecord<String, OrderRequestDTO> record = new ConsumerRecord<>("topic", 0, 0, "key", orderRequestDTO);

        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
        when(pdfGeneratorUtil.generatePdf(any(OrderRequestDTO.class))).thenReturn(pdfStream);
        doNothing().when(emailSenderService).sendEmail(anyString(), anyString(), anyString(), any(byte[].class));

        // Act
        emailNotificationService.listenOrderRequests(record);

        // Assert
        verify(pdfGeneratorUtil).generatePdf(orderRequestDTO);
        verify(emailSenderService).sendEmail(
                eq(orderRequestDTO.getReceiverEmail()),
                anyString(),
                anyString(),
                any(byte[].class)
        );
    }

    @Test
    void listenOrderRequests_HandlesException() throws MessagingException {
        // Arrange
        OrderRequestDTO orderRequestDTO = createTestOrderRequestDTO();
        ConsumerRecord<String, OrderRequestDTO> record = new ConsumerRecord<>("topic", 0, 0, "key", orderRequestDTO);

        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
        when(pdfGeneratorUtil.generatePdf(any(OrderRequestDTO.class))).thenReturn(pdfStream);
        doThrow(new MessagingException("Test exception")).when(emailSenderService)
                .sendEmail(anyString(), anyString(), anyString(), any(byte[].class));

        // Act
        emailNotificationService.listenOrderRequests(record);

        // Assert
        verify(pdfGeneratorUtil).generatePdf(orderRequestDTO);
        verify(emailSenderService).sendEmail(
                eq(orderRequestDTO.getReceiverEmail()),
                anyString(),
                anyString(),
                any(byte[].class)
        );
    }

    @Test
    void processOrderRequest_Success() throws MessagingException {
        // Arrange
        OrderRequestDTO orderRequestDTO = createTestOrderRequestDTO();
        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
        when(pdfGeneratorUtil.generatePdf(any(OrderRequestDTO.class))).thenReturn(pdfStream);
        doNothing().when(emailSenderService).sendEmail(anyString(), anyString(), anyString(), any(byte[].class));

        // Act
        emailNotificationService.processOrderRequest(orderRequestDTO);

        // Assert
        verify(pdfGeneratorUtil).generatePdf(orderRequestDTO);
        verify(emailSenderService).sendEmail(
                eq(orderRequestDTO.getReceiverEmail()),
                eq("Order Details - Shipment: " + orderRequestDTO.getShipmentNumber()),
                eq("Please find your order details attached."),
                any(byte[].class)
        );
    }

    @Test
    void processOrderRequest_EmailThrowsException() throws MessagingException {
        // Arrange
        OrderRequestDTO orderRequestDTO = createTestOrderRequestDTO();
        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
        when(pdfGeneratorUtil.generatePdf(any(OrderRequestDTO.class))).thenReturn(pdfStream);
        doThrow(new MessagingException("Test exception")).when(emailSenderService)
                .sendEmail(anyString(), anyString(), anyString(), any(byte[].class));

        // Act & Assert
        assertThrows(MessagingException.class, () -> {
            emailNotificationService.processOrderRequest(orderRequestDTO);
        });

        verify(pdfGeneratorUtil).generatePdf(orderRequestDTO);
        verify(emailSenderService).sendEmail(
                eq(orderRequestDTO.getReceiverEmail()),
                anyString(),
                anyString(),
                any(byte[].class)
        );
    }

    private OrderRequestDTO createTestOrderRequestDTO() {
        OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
        orderRequestDTO.setReceiverEmail("receiver@example.com");
        orderRequestDTO.setShipmentNumber("SHP12345");
        orderRequestDTO.setReceiverCountryCode("PL");
        orderRequestDTO.setSenderCountryCode("US");
        orderRequestDTO.setStatusCode(StatusCode.PROCESSING);
        return orderRequestDTO;
    }
}