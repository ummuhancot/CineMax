package com.cinemax.service.helper;

import com.cinemax.entity.concretes.business.Payment;
import com.cinemax.entity.concretes.business.Ticket;
import com.cinemax.entity.concretes.user.User;
import com.cinemax.entity.enums.PaymentStatus;
import com.cinemax.entity.enums.TicketStatus;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.repository.businnes.PaymentRepository;
import com.cinemax.repository.businnes.TicketRepository;
import com.cinemax.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentHelper {

    private final PaymentRepository paymentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public Ticket getTicketOrThrow(Long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Bilet bulunamadı. ID: " + ticketId));
    }

    public User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı. ID: " + userId));
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
