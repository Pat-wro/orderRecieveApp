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



@Service
@RequiredArgsConstructor
public class OrderEmailNotificationService {

    private final JavaMailSender mailSender;

    @KafkaListener(
            topics = MessageReadConst.Topics.EMAIL_TOPIC,
            groupId = MessageReadConst.Groups.EMAIL_REQUEST_GROUP,
            containerFactory = MessageReadConst.Listeners.MESSAGE_READ_LISTENER_CONTAINER_FACTORY
    )
    public void listenOrderRequests(ConsumerRecord<String, OrderRequestDTO> record) throws MessagingException {
        OrderRequestDTO orderRequest = record.value();
        processOrderRequest(orderRequest);
    }

    public void processOrderRequest(OrderRequestDTO orderRequest) throws MessagingException {
        ByteArrayOutputStream pdf = generatePdf(orderRequest);
        sendEmail(
                orderRequest.getReceiverEmail(),
                "Order Details - Shipment: " + orderRequest.getShipmentNumber(),
                "Please find your order details attached.",
                pdf.toByteArray()
        );
    }

    private ByteArrayOutputStream generatePdf(OrderRequestDTO orderRequest) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Dodanie nagłówka
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLACK);
            Paragraph header = new Paragraph("Order Details", headerFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            document.add(Chunk.NEWLINE);

            // Dodanie treści
            Font contentFont = new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL);

            document.add(new Paragraph("Shipment Number: " + orderRequest.getShipmentNumber(), contentFont));
            document.add(new Paragraph("Receiver Email: " + orderRequest.getReceiverEmail(), contentFont));
            document.add(new Paragraph("Receiver Country Code: " + orderRequest.getReceiverCountryCode(), contentFont));
            document.add(new Paragraph("Sender Country Code: " + orderRequest.getSenderCountryCode(), contentFont));
            document.add(new Paragraph("Status Code: " + orderRequest.getStatusCode(), contentFont));

            document.add(Chunk.NEWLINE);

            Font footerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC);
            Paragraph footer = new Paragraph("Thank you for using our service!", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error while generating PDF", e);
        }

        return out;
    }

    public void sendEmail(String to, String subject, String text, byte[] pdfAttachment) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);
        helper.addAttachment("order-details.pdf", new ByteArrayResource(pdfAttachment));
        mailSender.send(message);
    }
}
