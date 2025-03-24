package com.job.order_processing_app.converter;

import com.example.library.OrderRequestDTO;
import com.example.library.enums.StatusCode;
import com.job.order_processing_app.model.OrderCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.spi.MappingContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrderCommandToOrderRequestDtoConverterTest {

    private OrderCommandToOrderRequestDtoConverter converter;
    private MappingContext<OrderCommand, OrderRequestDTO> mappingContext;
    private OrderCommand orderCommand;

    @BeforeEach
    void setUp() {
        converter = new OrderCommandToOrderRequestDtoConverter();
        mappingContext = mock(MappingContext.class);

        orderCommand = OrderCommand.builder()
                .shipmentNumber("SHIP123")
                .receiverEmail("receiver@example.com")
                .receiverCountryCode("US")
                .senderCountryCode("CA")
                .statusCode(1)
                .build();

        when(mappingContext.getSource()).thenReturn(orderCommand);
    }

    @Test
    void convert_withValidOrderCommand_shouldMapAllFields() {
        OrderRequestDTO result = converter.convert(mappingContext);

        assertNotNull(result);
        assertEquals(orderCommand.getShipmentNumber(), result.getShipmentNumber());
        assertEquals(orderCommand.getReceiverEmail(), result.getReceiverEmail());
        assertEquals(orderCommand.getReceiverCountryCode(), result.getReceiverCountryCode());
        assertEquals(orderCommand.getSenderCountryCode(), result.getSenderCountryCode());

        assertNotNull(result.getStatusCode());
    }

    @Test
    void convert_withNullStatusCode_shouldReturnNullStatusCode() {
        orderCommand.setStatusCode(null);

        OrderRequestDTO result = converter.convert(mappingContext);

        assertNotNull(result);
        assertNull(result.getStatusCode());
    }

    @Test
    void convert_withInvalidStatusCode_shouldReturnNullStatusCode() {
        orderCommand.setStatusCode(999);

        OrderRequestDTO result = converter.convert(mappingContext);

        assertNotNull(result);
        assertNull(result.getStatusCode());
    }

    @Test
    void convert_withEachValidStatusCode_shouldMapCorrectly() {
        for (StatusCode statusCode : StatusCode.values()) {
            orderCommand.setStatusCode(statusCode.getCode());

            OrderRequestDTO result = converter.convert(mappingContext);

            assertEquals(statusCode, result.getStatusCode());
        }
    }
}