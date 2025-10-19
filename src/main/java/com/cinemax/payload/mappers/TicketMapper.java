package com.cinemax.payload.mappers;

import com.cinemax.entity.concretes.business.*;
import com.cinemax.entity.concretes.user.User;
import com.cinemax.payload.request.business.TicketRequest;
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

        String seatInfo;
        if (t.getSeatLetter() != null) {
            seatInfo = t.getSeatLetter();
        } else if (t.getHall() != null) {
            seatInfo = "Hall Seats: " + t.getHall().getSeatCapacity();
        } else {
            seatInfo = null;
        }

        return TicketResponse.builder()
                .id(t.getId())
                .username(t.getUser() != null ? t.getUser().getEmail() : null)
                .movieTitle(t.getMovie() != null ? t.getMovie().getTitle() : null)
                .hallName(t.getHall() != null ? t.getHall().getName() : null)
                .seat(seatInfo)
                .price(t.getPrice())
                .status(t.getTicketStatus() != null ? t.getTicketStatus().name() : null)
                .date(t.getShowtime() != null ? t.getShowtime().getDate() : null)
                .showTime(t.getShowtime() != null ? t.getShowtime().getStartTime() : null)
                .createdAt(t.getCreatedAt() != null ? t.getCreatedAt().toString() : null)
                .paymentId(t.getPayment() != null ? t.getPayment().getId() : null)
                .expiresAt(t.getExpiresAt() != null ? t.getExpiresAt() : null)
                .build();

    }


    /**
     * Mevcut Ticket entity'sini TicketRequest DTO ile günceller.
     * Sadece dolu (non-null) alanlar güncellenir.
     *
     * @param ticket Güncellenecek Ticket entity
     * @param request TicketRequest DTO
     * @param showTime ShowTime entity
     * @param hall Hall entity
     * @param movie Movie entity
     * @return Güncellenmiş Ticket entity
     */
    public Ticket updateFromRequest(Ticket ticket, TicketRequest request,
                                    ShowTime showTime, Hall hall, Movie movie) {

        if (request.getSeatLetter() != null) ticket.setSeatLetter(request.getSeatLetter());
        if (request.getSeatNumber() > 0) ticket.setSeatNumber(request.getSeatNumber());

        if (showTime != null) ticket.setShowtime(showTime);
        if (hall != null) ticket.setHall(hall);
        if (movie != null) ticket.setMovie(movie);

        if (request.getPrice() != null) ticket.setPrice(request.getPrice());
        return ticket;
    }
}
