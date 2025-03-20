package com.example.library.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor

public enum StatusCode {
    NEW(0, "Nowe zamówienie"),
    PROCESSING(1, "W trakcie przetwarzania"),
    PACKED(2, "Spakowane"),
    IN_TRANSIT(3, "W transporcie"),
    DELIVERED(4, "Dostarczone"),
    COMPLETED(5, "Zakończone");

    private final int code;
    private final String description;


    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
