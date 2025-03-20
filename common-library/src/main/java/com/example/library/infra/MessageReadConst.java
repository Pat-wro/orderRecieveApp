package com.example.library.infra;

public interface MessageReadConst {
    interface Listeners {
        String MESSAGE_READ_LISTENER_CONTAINER_FACTORY = "listenerContainerFactory";
    }

    interface Groups {
        String MESSAGE_READ_GROUP = "order-request-group";
        String EMAIL_REQUEST_GROUP = "email-request-group";
    }

    interface Topics {
        String ORDER_TOPIC = "orderDtoToMail";
        String EMAIL_TOPIC = "order-email-requests";
    }
}