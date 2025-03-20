package com.example.library;

import com.example.library.enums.StatusCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDTO {

    private String shipmentNumber;
    private String receiverEmail;
    private String receiverCountryCode;
    private String senderCountryCode;
    private StatusCode statusCode;
}