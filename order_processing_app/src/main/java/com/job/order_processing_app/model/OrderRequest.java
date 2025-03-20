package com.job.order_processing_app.model;

import com.example.library.enums.StatusCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class OrderRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID orderId;
    private String shipmentNumber;
    private String receiverEmail;
    private String receiverCountryCode;
    private String senderCountryCode;
    private StatusCode statusCode;
}