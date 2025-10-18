package com.cinemax.service.validator;

import com.cinemax.entity.concretes.business.Ticket;
import com.cinemax.entity.enums.TicketStatus;
import com.cinemax.exception.InvalidPaymentException;
import com.cinemax.exception.PaymentAlreadyProcessedException;
import com.cinemax.repository.businnes.TicketRepository;
import com.cinemax.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class PaymentValidator {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public void validateTicketReserved(Ticket ticket) {
        if (ticket.getTicketStatus() != TicketStatus.RESERVED) {
            throw new InvalidPaymentException("Bu bilet ödeme için uygun değil!");
        }
        if (ticket.getExpiresAt() != null && ticket.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidPaymentException("Rezervasyon süresi dolmuş.");
        }
    }

    public void validateAmountMatches(Ticket ticket, Double amount) {
        if (ticket.getPrice() == null || !Objects.equals(ticket.getPrice(), amount)) {
            throw new InvalidPaymentException("Ödenecek tutar bilet fiyatı ile eşleşmiyor!");
        }
    }

    public void validateNotAlreadyPaid(Ticket ticket) {
        if (ticket.getTicketStatus() == TicketStatus.PAID) {
            throw new PaymentAlreadyProcessedException("Bu bilet daha önce ödenmiş.");
        }
    }
}
