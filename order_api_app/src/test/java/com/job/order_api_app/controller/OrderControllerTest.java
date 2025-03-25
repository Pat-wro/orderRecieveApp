package com.job.order_api_app.controller;

import com.example.library.OrderRequestDTO;
import com.example.library.enums.StatusCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.job.order_api_app.model.OrderCommand;
import com.job.order_api_app.service.OrderMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderMessageService orderMessageService;

    @MockBean
    private ModelMapper modelMapper;

    private OrderCommand validOrderCommand;
    private OrderRequestDTO orderRequestDTO;

    @BeforeEach
    void setUp() {
        validOrderCommand = OrderCommand.builder()
                .shipmentNumber("SHIP123")
                .receiverEmail("receiver@example.com")
                .receiverCountryCode("US")
                .senderCountryCode("CA")
                .statusCode(1)
                .build();

        orderRequestDTO = OrderRequestDTO.builder()
                .shipmentNumber("SHIP123")
                .receiverEmail("receiver@example.com")
                .receiverCountryCode("US")
                .senderCountryCode("CA")
                .statusCode(StatusCode.PROCESSING)
                .build();

        when(modelMapper.map(any(OrderCommand.class), eq(OrderRequestDTO.class)))
                .thenReturn(orderRequestDTO);
    }

    @Test
    void handleOrder_withValidCommand_shouldReturnCreated() throws Exception {
        doNothing().when(orderMessageService).processOrder(any(OrderRequestDTO.class));

        mockMvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validOrderCommand)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("Order received and processed."));

        verify(orderMessageService, times(1)).processOrder(eq(orderRequestDTO));
    }

    @Test
    void handleOrder_withInvalidCommand_shouldReturnBadRequest() throws Exception {
        OrderCommand invalidCommand = OrderCommand.builder()
                .shipmentNumber("")  // Invalid: empty string
                .receiverEmail("receiver@example.com")
                .receiverCountryCode("US")
                .senderCountryCode("CA")
                .statusCode(1)
                .build();

        mockMvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCommand)))
                .andExpect(status().isBadRequest());

        verify(orderMessageService, never()).processOrder(any());
    }

    @Test
    void handleOrder_withStatusCodeOutOfRange_shouldReturnBadRequest() throws Exception {
        OrderCommand invalidStatusCommand = OrderCommand.builder()
                .shipmentNumber("SHIP123")
                .receiverEmail("receiver@example.com")
                .receiverCountryCode("US")
                .senderCountryCode("CA")
                .statusCode(10)
                .build();

        mockMvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidStatusCommand)))
                .andExpect(status().isBadRequest());

        verify(orderMessageService, never()).processOrder(any());
    }

    @Test
    void handleOrder_withNullFields_shouldReturnBadRequest() throws Exception {
        OrderCommand nullFieldsCommand = OrderCommand.builder()
                .shipmentNumber("SHIP123")
                .receiverEmail(null)
                .receiverCountryCode("US")
                .senderCountryCode("CA")
                .statusCode(1)
                .build();

        mockMvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nullFieldsCommand)))
                .andExpect(status().isBadRequest());

        verify(orderMessageService, never()).processOrder(any());
    }

    @Test
    void handleOrder_shouldMapCommandToDto() throws Exception {
        doNothing().when(orderMessageService).processOrder(any(OrderRequestDTO.class));

        ArgumentCaptor<OrderCommand> commandCaptor = ArgumentCaptor.forClass(OrderCommand.class);

        mockMvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validOrderCommand)))
                .andExpect(status().isCreated());

        verify(modelMapper).map(commandCaptor.capture(), eq(OrderRequestDTO.class));
        OrderCommand capturedCommand = commandCaptor.getValue();

        assertEquals(validOrderCommand.getShipmentNumber(), capturedCommand.getShipmentNumber());
        assertEquals(validOrderCommand.getReceiverEmail(), capturedCommand.getReceiverEmail());
        assertEquals(validOrderCommand.getReceiverCountryCode(), capturedCommand.getReceiverCountryCode());
        assertEquals(validOrderCommand.getSenderCountryCode(), capturedCommand.getSenderCountryCode());
        assertEquals(validOrderCommand.getStatusCode(), capturedCommand.getStatusCode());
    }
}