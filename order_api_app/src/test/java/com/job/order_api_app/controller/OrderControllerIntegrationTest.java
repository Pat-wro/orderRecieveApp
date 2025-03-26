package com.job.order_api_app.controller;

import com.example.library.OrderRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.job.order_api_app.model.OrderCommand;
import com.job.order_api_app.service.OrderMessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class OrderControllerIntegrationTest {

    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.3.0"));

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @SpyBean
    private OrderMessageService orderMessageService;

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @Test
    void shouldProcessOrderSuccessfully() throws Exception {
        OrderCommand orderCommand = createSampleOrderCommand();
        String orderCommandJson = objectMapper.writeValueAsString(orderCommand);

        mockMvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderCommandJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("Order received and processed."));

        verify(orderMessageService, times(1)).processOrder(any(OrderRequestDTO.class));
    }

    @Test
    void shouldReturnBadRequestWhenInvalidOrderData() throws Exception {
        OrderCommand invalidOrderCommand = new OrderCommand();
        String invalidOrderCommandJson = objectMapper.writeValueAsString(invalidOrderCommand);

        mockMvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidOrderCommandJson))
                .andExpect(status().isBadRequest());
    }

    private OrderCommand createSampleOrderCommand() {
        return OrderCommand.builder()
                .shipmentNumber("SHIP-" + UUID.randomUUID().toString().substring(0, 8))
                .receiverEmail("odbiorca@example.com")
                .receiverCountryCode("PL")
                .senderCountryCode("DE")
                .statusCode(1)
                .build();
    }
}