package com.cinemax.entity.enums;

public enum TicketStatus {
    RESERVED("Reserved"),
    CANCELLED("Cancelled"),
    PAID("Paid");

    private final String label;

    TicketStatus(String label) {
        this.label = label;
    }

}