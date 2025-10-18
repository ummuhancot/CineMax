package com.cinemax.payload.request.business;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@NoArgsConstructor
@SuperBuilder
public class PaymentRequest {

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotNull(message = "Ticket ID cannot be null")
    private Long ticketId;

    @NotNull(message = "Amount cannot be null")
    private Double amount;
}
