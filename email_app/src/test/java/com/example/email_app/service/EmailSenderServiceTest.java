package com.example.email_app.service;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
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

class EmailSenderServiceTest {

    @InjectMocks
    private EmailSenderService emailSenderService;

    @Mock
    private JavaMailSender mailSender;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendEmail_Success() throws MessagingException {
        String to = "receiver@example.com";
        String subject = "Order Details";
        String text = "Please find your order details attached.";
        byte[] pdfAttachment = new byte[]{1, 2, 3};

        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
        doNothing().when(mailSender).send(any(MimeMessage.class));

        emailSenderService.sendEmail(to, subject, text, pdfAttachment);

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

        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
        doAnswer((Answer<Void>) invocation -> {
            throw new MessagingException("Test exception");
        }).when(mailSender).send(any(MimeMessage.class));

        // Act & Assert
        assertThrows(MessagingException.class, () -> {
            emailSenderService.sendEmail(to, subject, text, pdfAttachment);
        });

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }
}