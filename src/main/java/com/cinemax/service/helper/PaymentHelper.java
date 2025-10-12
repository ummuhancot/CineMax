package com.cinemax.service.helper;

import com.cinemax.entity.concretes.business.Payment;
import com.cinemax.entity.concretes.business.Ticket;
import com.cinemax.entity.enums.PaymentStatus;
import com.cinemax.entity.enums.TicketStatus;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.mappers.PaymentMapper;
import com.cinemax.repository.businnes.PaymentRepository;
import com.cinemax.repository.businnes.TicketRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentHelper {

    private final PaymentRepository paymentRepository;
    private final TicketRepository ticketRepository;

    public Ticket getTicketOrThrow(Long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + ticketId));
    }

    public Payment getPaymentOrThrow(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));
    }

    public Payment completePaymentLogic(Payment payment, boolean success) {
        if (success) {
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            payment.getTicket().setTicketStatus(TicketStatus.PAID);
        } else {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.getTicket().setTicketStatus(TicketStatus.CANCELLED);
        }
        return payment;
    }
}
