package com.job.order_processing_app.converter;

import com.example.library.OrderRequestDTO;
import com.example.library.enums.StatusCode;
import com.job.order_processing_app.model.OrderCommand;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Service;

@Service
public class OrderCommandToOrderRequestDtoConverter implements Converter<OrderCommand, OrderRequestDTO> {

    @Override
    public OrderRequestDTO convert(MappingContext<OrderCommand, OrderRequestDTO> mappingContext) {
        OrderCommand command = mappingContext.getSource();

        StatusCode statusCode = null;
        if (command.getStatusCode() != null) {
            for (StatusCode code : StatusCode.values()) {
                if (code.getCode() == command.getStatusCode()) {
                    statusCode = code;
                    break;
                }
            }
        }
        return OrderRequestDTO.builder()
                .shipmentNumber(command.getShipmentNumber())
                .receiverEmail(command.getReceiverEmail())
                .receiverCountryCode(command.getReceiverCountryCode())
                .senderCountryCode(command.getSenderCountryCode())
                .statusCode(statusCode)
                .build();
    }
}
