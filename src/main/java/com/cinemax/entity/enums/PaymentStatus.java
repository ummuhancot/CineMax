package com.cinemax.entity.enums;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PENDING("Pending"),
    SUCCESS("Success"),
    FAILED("Failed");

    private final String label;

    PaymentStatus(String label) {
        this.label = label;
    }

}
