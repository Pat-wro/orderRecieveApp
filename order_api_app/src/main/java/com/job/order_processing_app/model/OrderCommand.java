package com.job.order_processing_app.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCommand {

    @NotBlank(message = "shipment number is mandatory")
    private String shipmentNumber;
    @NotBlank(message = "email is mandatory")
    private String receiverEmail;
    @NotBlank(message = "receiver code is mandatory")
    private String receiverCountryCode;
    @NotBlank(message = "sender code is mandatory")
    private String senderCountryCode;
    @NotNull(message = "status code cannot be null")
    @Min(value = 0, message = "Value must be at least 0")
    @Max(value = 5, message = "Value cannot exceed 5")
    private Integer statusCode;
}
