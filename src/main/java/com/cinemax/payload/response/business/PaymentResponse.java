package com.cinemax.payload.response.business;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponse {

    private Long id;
    private Double amount;
    private Double change;          // Para üstü (amount - ticket price)
    private String paymentStatus;
    private LocalDateTime paymentDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private TicketResponse ticket;
}
