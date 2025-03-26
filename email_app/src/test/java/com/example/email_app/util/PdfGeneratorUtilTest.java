package com.example.email_app.util;

import com.example.library.OrderRequestDTO;
import com.example.library.enums.StatusCode;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class PdfGeneratorUtilTest {

    private final PdfGeneratorUtil pdfGeneratorUtil = new PdfGeneratorUtil();

    @Test
    void generatePdf_ReturnsNonEmptyByteStream() {
        OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
        orderRequestDTO.setShipmentNumber("SHP12345");
        orderRequestDTO.setReceiverEmail("test@example.com");
        orderRequestDTO.setReceiverCountryCode("PL");
        orderRequestDTO.setSenderCountryCode("US");
        orderRequestDTO.setStatusCode(StatusCode.PROCESSING);

        ByteArrayOutputStream result = pdfGeneratorUtil.generatePdf(orderRequestDTO);

        assertNotNull(result);
        assertTrue(result.size() > 0, "PDF should contain data");
    }
}