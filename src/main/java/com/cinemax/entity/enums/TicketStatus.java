package com.cinemax.entity.enums;

public enum TicketStatus {
    RESERVED("Reserved"),
    CANCELLED("Cancelled"),
    PAID("Paid"),
    USED("Used");

    private final String label;

    TicketStatus(String label) {
        this.label = label;
    }

}
//Reserved,Paid,Cancelled