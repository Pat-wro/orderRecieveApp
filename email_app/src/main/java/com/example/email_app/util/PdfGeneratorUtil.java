package com.example.email_app.util;

import com.example.library.OrderRequestDTO;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
public class PdfGeneratorUtil {

    public ByteArrayOutputStream generatePdf(OrderRequestDTO orderRequest) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font headerFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLACK);
            Paragraph header = new Paragraph("Order Details", headerFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            document.add(Chunk.NEWLINE);

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
}
