package com.cinemax.payload.mappers;

import com.cinemax.entity.concretes.business.Ticket;
import com.cinemax.entity.enums.TicketStatus;
import com.cinemax.payload.response.business.TicketResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicketMapper {



    /**
     * Ticket entity → TicketResponse dönüşümü
     */


    public TicketResponse mapTicketToResponse(Ticket ticket) {
        if (ticket == null) {
            return null;
        }

        String statusLabel = ticket.getTicketStatus() != null
                ? ticket.getTicketStatus().getLabel()
                : TicketStatus.RESERVED.getLabel();

        String seat = (ticket.getSeatLetter() != null ? ticket.getSeatLetter() : "")
                + ticket.getSeatNumber();

        return TicketResponse.builder()
                .id(ticket.getId())
                .username(ticket.getUser() != null ? ticket.getUser().getEmail() : null)
                .movieTitle(ticket.getMovie() != null ? ticket.getMovie().getTitle() : null)
                .hallName(ticket.getHall() != null ? ticket.getHall().getName() : null)
                .seat(seat)
                .price(ticket.getPrice())
                .status(statusLabel)
                .date(ticket.getShowtime() != null && ticket.getShowtime().getDate() != null
                        ? ticket.getShowtime().getDate().toString()
                        : null)
                .showTime(ticket.getShowtime() != null
                        ? ticket.getShowtime().getStartTime() + " - " + ticket.getShowtime().getEndTime()
                        : null)
                .paymentId(ticket.getPayment() != null ? ticket.getPayment().getId() : null)
                .createdAt(ticket.getCreatedAt() != null ? ticket.getCreatedAt().toString() : null)
                .expiresAt(ticket.getExpiresAt())
                .build();
    }





}
