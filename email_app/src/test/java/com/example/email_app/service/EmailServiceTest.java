package com.example.email_app.service;

import com.example.library.OrderRequestDTO;
import com.example.library.enums.StatusCode;
import com.itextpdf.text.pdf.PdfReader;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderEmailNotificationServiceTest {

    @InjectMocks
    private OrderEmailNotificationService emailService;

    @Mock
    private JavaMailSender mailSender;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listenOrderRequests_Success() throws MessagingException {
        // Arrange
        OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
        orderRequestDTO.setReceiverEmail("receiver@example.com");
        orderRequestDTO.setShipmentNumber("SHP12345");
        orderRequestDTO.setReceiverCountryCode("PL");
        orderRequestDTO.setSenderCountryCode("US");
        orderRequestDTO.setStatusCode(StatusCode.PROCESSING);

        ConsumerRecord<String, OrderRequestDTO> record = new ConsumerRecord<>("topic", 0, 0, "key", orderRequestDTO);

        // Mock the mail sender
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        emailService.listenOrderRequests(record);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void listenOrderRequests_ExceptionInProcessing() throws MessagingException {
        // Arrange
        OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
        orderRequestDTO.setReceiverEmail("receiver@example.com");

        ConsumerRecord<String, OrderRequestDTO> record = new ConsumerRecord<>("topic", 0, 0, "key", orderRequestDTO);

        // Setup the mock to throw an exception
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Test exception"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> emailService.listenOrderRequests(record));
        verify(mailSender).createMimeMessage();
        verifyNoMoreInteractions(mailSender);
    }

    @Test
    void processOrderRequest_Success() throws MessagingException {
        // Arrange
        OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
        orderRequestDTO.setReceiverEmail("receiver@example.com");
        orderRequestDTO.setShipmentNumber("SHP12345");
        orderRequestDTO.setReceiverCountryCode("PL");
        orderRequestDTO.setSenderCountryCode("US");
        orderRequestDTO.setStatusCode(StatusCode.PROCESSING);

        // Mock the mail sender
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        emailService.processOrderRequest(orderRequestDTO);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void processOrderRequest_EmailThrowsException() throws MessagingException {
        // Arrange
        OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
        orderRequestDTO.setReceiverEmail("receiver@example.com");
        orderRequestDTO.setShipmentNumber("SHP12345");
        orderRequestDTO.setReceiverCountryCode("PL");
        orderRequestDTO.setSenderCountryCode("US");
        orderRequestDTO.setStatusCode(StatusCode.PROCESSING);

        // Mock the mail sender to throw an exception
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

        // Użycie doAnswer zamiast doThrow dla wyjątku typu checked
        doAnswer(invocation -> {
            throw new MessagingException("Test exception");
        }).when(mailSender).send(any(MimeMessage.class));

        // Act & Assert
        assertThrows(MessagingException.class, () -> {
            emailService.processOrderRequest(orderRequestDTO);
        });

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    // Zamiast testować prywatną metodę generatePdf bezpośrednio,
    // testujemy jej efekt poprzez metodę processOrderRequest
    @Test
    void processOrderRequest_GeneratesPdf() throws MessagingException {
        // Arrange
        OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
        orderRequestDTO.setReceiverEmail("receiver@example.com");
        orderRequestDTO.setShipmentNumber("SHP12345");
        orderRequestDTO.setReceiverCountryCode("PL");
        orderRequestDTO.setSenderCountryCode("US");
        orderRequestDTO.setStatusCode(StatusCode.PROCESSING);

        // Setup to capture the PDF attachment
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

        // Act
        emailService.processOrderRequest(orderRequestDTO);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
        // Nie możemy bezpośrednio zweryfikować zawartości PDF bez refleksji,
        // ale możemy sprawdzić, czy metoda wysyłająca e-mail została wywołana
        // z odpowiednimi argumentami
    }

    @Test
    void sendEmail_Success() throws MessagingException {
        // Arrange
        String to = "receiver@example.com";
        String subject = "Order Details";
        String text = "Please find your order details attached.";
        byte[] pdfAttachment = new byte[]{1, 2, 3};

        // Mock the mail sender
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        emailService.sendEmail(to, subject, text, pdfAttachment);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void sendEmail_MessagingException() {
        // Arrange
        String to = "receiver@example.com";
        String subject = "Order Details";
        String text = "Please find your order details attached.";
        byte[] pdfAttachment = new byte[]{1, 2, 3};

        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Simulate a MessagingException
        doAnswer((Answer<Void>) invocation -> {
            throw new MessagingException("Test exception");
        }).when(mailSender).send(any(MimeMessage.class));

        // Act & Assert
        assertThrows(MessagingException.class, () -> {
            emailService.sendEmail(to, subject, text, pdfAttachment);
        });

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }
}



