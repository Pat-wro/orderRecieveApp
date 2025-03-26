package com.example.email_app.service;

import com.example.library.OrderRequestDTO;
import com.example.library.infra.MessageReadConst;
import com.itextpdf.text.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;



import com.itextpdf.text.pdf.PdfWriter;



import com.example.email_app.util.PdfGeneratorUtil;
import com.example.library.OrderRequestDTO;
import com.example.library.infra.MessageReadConst;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEmailNotificationService {

    private final PdfGeneratorUtil pdfGeneratorUtil;
    private final EmailSenderService emailSenderService;

    @KafkaListener(
            topics = MessageReadConst.Topics.EMAIL_TOPIC,
            groupId = MessageReadConst.Groups.EMAIL_REQUEST_GROUP,
            containerFactory = MessageReadConst.Listeners.MESSAGE_READ_LISTENER_CONTAINER_FACTORY
    )
    public void listenOrderRequests(ConsumerRecord<String, OrderRequestDTO> record) {
        OrderRequestDTO orderRequest = record.value();
        try {
            processOrderRequest(orderRequest);
        } catch (MessagingException e) {
            log.error("Failed to send email for order: {}", orderRequest.getShipmentNumber(), e);
        }
    }

    public void processOrderRequest(OrderRequestDTO orderRequest) throws MessagingException {
        ByteArrayOutputStream pdf = pdfGeneratorUtil.generatePdf(orderRequest);
        emailSenderService.sendEmail(
                orderRequest.getReceiverEmail(),
                "Order Details - Shipment: " + orderRequest.getShipmentNumber(),
                "Please find your order details attached.",
                pdf.toByteArray()
        );
    }
}
