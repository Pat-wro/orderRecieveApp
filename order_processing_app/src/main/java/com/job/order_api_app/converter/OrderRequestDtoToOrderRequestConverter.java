package com.job.order_api_app.converter;

import com.example.library.OrderRequestDTO;
import com.job.order_api_app.model.OrderRequest;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Service;

@Service
public class OrderRequestDtoToOrderRequestConverter implements Converter<OrderRequestDTO, OrderRequest> {

    @Override
    public OrderRequest convert(MappingContext<OrderRequestDTO, OrderRequest> mappingContext) {
        OrderRequestDTO dto = mappingContext.getSource();

        return OrderRequest.builder()
                .shipmentNumber(dto.getShipmentNumber())
                .receiverEmail(dto.getReceiverEmail())
                .receiverCountryCode(dto.getReceiverCountryCode())
                .senderCountryCode(dto.getSenderCountryCode())
                .statusCode(dto.getStatusCode())
                .build();
    }
}
