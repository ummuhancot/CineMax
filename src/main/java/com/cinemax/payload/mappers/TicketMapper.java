package com.cinemax.payload.mappers;

import com.cinemax.entity.concretes.business.*;
import com.cinemax.entity.concretes.user.User;
import com.cinemax.payload.request.business.TicketRequest;
import com.cinemax.payload.response.business.PaymentResponse;
import com.cinemax.payload.response.business.TicketResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TicketMapper {



    /**
     * TicketRequest DTO → Ticket entity dönüşümü
     */
    public Ticket toEntity(TicketRequest request, User user, Movie movie, Hall hall, ShowTime showTime, int defaultMinutes) {
        return Ticket.builder()
                .user(user)
                .movie(movie)
                .hall(hall)
                .showtime(showTime)
                .seatLetter(request.getSeatLetter())
                .seatNumber(request.getSeatNumber())
                .price(request.getPrice())
                .ticketStatus(null) // StatusManager ile set edilecek
                .expiresAt(LocalDateTime.now().plusMinutes(defaultMinutes))
                .build();
    }

    /**
     * Ticket entity → TicketResponse DTO dönüşümü
     */

    // Ticket entity → TicketResponse
    public TicketResponse toResponse(Ticket t) {
        if (t == null) return null;

        return TicketResponse.builder()
                .id(t.getId())
                .username(t.getUser() != null ? t.getUser().getEmail() : null)
                .movieTitle(t.getMovie() != null ? t.getMovie().getTitle() : null)
                .hallName(t.getHall() != null ? t.getHall().getName() : null)
                .seat(t.getSeatLetter() != null ? t.getSeatLetter()
                        : (t.getHall() != null ? "Hall Seats: " + t.getHall().getSeatCapacity() : null))
                .price(t.getPrice())
                .status(t.getTicketStatus() != null ? t.getTicketStatus().name() : null)
                .date(t.getShowtime() != null && t.getShowtime().getStartTime() != null
                        ? t.getShowtime().getStartTime().toString()
                        : null)
                .showTime(t.getShowtime() != null && t.getShowtime().getStartTime() != null
                        ? t.getShowtime().getStartTime().toString()
                        : null)
                .createdAt(t.getCreatedAt() != null ? t.getCreatedAt().toString() : null)
                .paymentId(t.getPayment() != null ? t.getPayment().getId() : null)
                .expiresAt(t.getExpiresAt() != null ? t.getExpiresAt() : null)
                .build();
    }
}
