package com.cinemax.payload.mappers;

import com.cinemax.entity.concretes.business.Payment;
import com.cinemax.entity.concretes.business.Ticket;
import com.cinemax.entity.concretes.user.User;
import com.cinemax.entity.enums.PaymentStatus;
import com.cinemax.payload.request.business.PaymentRequest;
import com.cinemax.payload.response.business.PaymentResponse;
import com.cinemax.payload.response.business.TicketResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PaymentMapper {

    public Payment toEntity(PaymentRequest request, User user, Ticket ticket) {
        return Payment.builder()
                .user(user)
                .ticket(ticket)
                .amount(request.getAmount())
                .paymentDate(LocalDateTime.now())
                .paymentStatus(PaymentStatus.PENDING) // İşlem sonrası update edilecek
                .build();
    }


    // Request + ilişkili nesnelerden Payment entity oluştur

    // Payment entity → PaymentResponse (içinde TicketResponse)
    public PaymentResponse toResponse(Payment payment, TicketMapper ticketMapper, Double change) {
        if (payment == null) return null;

        TicketResponse ticketResponse = ticketMapper.toResponse(payment.getTicket());

        return PaymentResponse.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .change(change)                .paymentStatus(payment.getPaymentStatus() != null ? payment.getPaymentStatus().name() : null)
                .paymentDate(payment.getPaymentDate())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .ticket(ticketResponse)
                .build();
    }
}
