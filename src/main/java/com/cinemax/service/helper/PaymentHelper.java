package com.cinemax.service.helper;

import com.cinemax.entity.concretes.business.Payment;
import com.cinemax.entity.concretes.business.Ticket;
import com.cinemax.entity.concretes.user.User;
import com.cinemax.entity.enums.PaymentStatus;
import com.cinemax.entity.enums.TicketStatus;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.messages.ErrorMessages;
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
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessages.TICKET_NOT_FOUND + ticketId));
    }

    public User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessages.USER_NOT_FOUND + userId));
    }



    public Payment getPaymentOrThrow(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.PAYMENT_NOT_FOUND + paymentId));
    }

}
