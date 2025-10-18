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

    private Long userId;

    @NotNull(message = "Ticket ID cannot be null")
    private Long ticketId;

    private Double amount;

    private Boolean success;
}
