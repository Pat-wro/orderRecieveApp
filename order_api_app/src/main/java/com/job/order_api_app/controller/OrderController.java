package com.job.order_api_app.controller;


import com.example.library.OrderRequestDTO;
import com.job.order_api_app.model.ApiResponse;
import com.job.order_api_app.model.OrderCommand;
import com.job.order_api_app.service.OrderMessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderMessageService orderMessageService;
    private final ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> handleOrder(@Valid @RequestBody OrderCommand orderCommand) {
        OrderRequestDTO orderRequestDTO = modelMapper.map(orderCommand, OrderRequestDTO.class);
        orderMessageService.processOrder(orderRequestDTO);
        ApiResponse<String> response = ApiResponse.success("Order received and processed.");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}