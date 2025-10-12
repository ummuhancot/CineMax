package com.cinemax.payload.response.business;

import com.cinemax.entity.enums.PaymentStatus;
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

    private Long userId;
    private Long paymentId;
    private Long ticketId;
    private Double amount;
    private PaymentStatus paymentStatus;
    private LocalDateTime paymentDate;
}
