package com.cinemax.service.statusmanager;

import com.cinemax.entity.concretes.business.Ticket;
import com.cinemax.entity.enums.TicketStatus;
import com.cinemax.repository.businnes.TicketRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TicketStatusManager {

    private final TicketRepository ticketRepository;

    @Transactional
    public Ticket setReserved(Ticket ticket, int minutes) {
        ticket.setTicketStatus(TicketStatus.RESERVED);
        ticket.setExpiresAt(LocalDateTime.now().plusMinutes(minutes));
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket setPaid(Ticket ticket) {
        ticket.setTicketStatus(TicketStatus.PAID);
        ticket.setExpiresAt(null);
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket setCancelled(Ticket ticket) {
        ticket.setTicketStatus(TicketStatus.CANCELLED);
        ticket.setExpiresAt(null);
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket setExpired(Ticket ticket) {
        ticket.setTicketStatus(TicketStatus.CANCELLED);
        ticket.setExpiresAt(null);
        return ticketRepository.save(ticket);
    }
}
