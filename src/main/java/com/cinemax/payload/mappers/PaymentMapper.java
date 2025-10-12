package com.cinemax.payload.mappers;

import com.cinemax.entity.concretes.business.Payment;
import com.cinemax.entity.concretes.business.Ticket;
import com.cinemax.entity.enums.PaymentStatus;
import com.cinemax.payload.response.business.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PaymentMapper {

    // Ticket -> Payment entity
    public Payment toEntity(Ticket ticket) {
        return Payment.builder()
                .ticket(ticket)
                .user(ticket.getUser())
                .amount(ticket.getPrice())
                .paymentStatus(com.cinemax.entity.enums.PaymentStatus.PENDING)
                .paymentDate(LocalDateTime.now())
                .build();
    }

    // Payment -> Response
    public PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .userId(payment.getUser() != null ? payment.getUser().getId() : null)
                .ticketId(payment.getTicket() != null ? payment.getTicket().getId() : null)
                .amount(payment.getAmount())
                .paymentStatus(payment.getPaymentStatus())
                .paymentDate(payment.getPaymentDate())
                .build();
    }
}
