package com.cinemax.service.validator;

import com.cinemax.entity.concretes.business.Ticket;
import com.cinemax.entity.enums.TicketStatus;
import com.cinemax.exception.InvalidRequestException;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.repository.businnes.TicketRepository;
import com.cinemax.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentValidator {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public Ticket validateTicketAndUser(Long ticketId, Long userId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));

        if (!ticket.getUser().getId().equals(userId)) {
            throw new InvalidRequestException("Ticket does not belong to this user");
        }

        if (ticket.getTicketStatus() != TicketStatus.RESERVED) {
            throw new InvalidRequestException("Ticket is not in RESERVED status");
        }

        return ticket;
    }
}
