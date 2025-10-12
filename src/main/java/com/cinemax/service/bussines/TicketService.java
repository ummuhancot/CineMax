package com.cinemax.service.bussines;

import com.cinemax.entity.concretes.business.*;
import com.cinemax.entity.concretes.user.User;
import com.cinemax.entity.enums.TicketStatus;
import com.cinemax.exception.InvalidRequestException;
import com.cinemax.payload.mappers.TicketMapper;
import com.cinemax.payload.messages.ErrorMessages;
import com.cinemax.payload.request.business.TicketRequest;
import com.cinemax.payload.response.business.TicketResponse;
import com.cinemax.repository.businnes.TicketRepository;
import com.cinemax.service.helper.TicketHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketHelper ticketHelper;
    private final TicketMapper ticketMapper;


    private static final int DEFAULT_RESERVATION_HOURS = 10;

    @Transactional
    public TicketResponse reserveTicket(TicketRequest request) {
        ticketHelper.getUserOrThrow(request.getUserId());
        ticketHelper.getMovieOrThrow(request.getMovieId());
        ticketHelper.getHallOrThrow(request.getHallId());
        ticketHelper.getShowTimeOrThrow(request.getShowTimeId());

        ticketHelper.checkSeatAvailability(
                request.getHallId(),
                request.getShowTimeId(),
                request.getSeatLetter(),
                request.getSeatNumber()
        );

        Ticket ticket = reserveTicketInternal(
                request.getMovieId(),
                request.getHallId(),
                request.getShowTimeId(),
                request.getUserId(),
                request.getPrice(),
                request.getSeatLetter(),
                request.getSeatNumber()
        );

        return ticketMapper.mapTicketToResponse(ticket);
    }

    // 🔹 2️⃣ Çoklu koltuk rezervasyonu
    @Transactional
    public List<TicketResponse> reserveTickets(TicketRequest request) {
        // Entity kontrolleri
        ticketHelper.getUserOrThrow(request.getUserId());
        ticketHelper.getMovieOrThrow(request.getMovieId());
        ticketHelper.getHallOrThrow(request.getHallId());
        ticketHelper.getShowTimeOrThrow(request.getShowTimeId());

        if (request.getSeats() == null || request.getSeats().isEmpty()) {
            throw new InvalidRequestException(ErrorMessages.SEAT_NOT_SELECTED);
        }

        List<Ticket> createdTickets = new ArrayList<>();

        // Her koltuğu sırayla rezerve et
        for (String seat : request.getSeats()) {
            Map<String, Object> parsedSeat = ticketHelper.parseSeat(seat);
            String seatLetter = (String) parsedSeat.get("seatLetter");
            int seatNumber = (Integer) parsedSeat.get("seatNumber");

            ticketHelper.checkSeatAvailability(request.getHallId(), request.getShowTimeId(), seatLetter, seatNumber);

            Ticket ticket = reserveTicketInternal(
                    request.getMovieId(),
                    request.getHallId(),
                    request.getShowTimeId(),
                    request.getUserId(),
                    request.getPrice(),
                    seatLetter,
                    seatNumber
            );

            createdTickets.add(ticket);
        }

        // TicketResponse listesi dön
        return createdTickets.stream()
                .map(ticketMapper::mapTicketToResponse)
                .toList();
    }

    // 🔹 3️⃣ Ortak internal metod
    @Transactional
    public Ticket reserveTicketInternal(Long movieId, Long hallId, Long showTimeId, Long userId,
                                        Double price, String seatLetter, int seatNumber) {

        User user = ticketHelper.getUserOrThrow(userId);
        Movie movie = ticketHelper.getMovieOrThrow(movieId);
        Hall hall = ticketHelper.getHallOrThrow(hallId);
        ShowTime showTime = ticketHelper.getShowTimeOrThrow(showTimeId);

        // Rezervasyon olduğu için status sabit: RESERVED
        TicketStatus ticketStatus = TicketStatus.RESERVED;

        Ticket ticket = Ticket.builder()
                .user(user)
                .movie(movie)
                .hall(hall)
                .showtime(showTime)
                .seatLetter(seatLetter)
                .seatNumber(seatNumber)
                .ticketStatus(ticketStatus)
                .price(price)
                .expiresAt(LocalDateTime.now().plusSeconds(DEFAULT_RESERVATION_HOURS)) //plusSeconds plusHours
                .build();

        return ticketRepository.save(ticket);
    }
    private List<TicketResponse> getAllTicketsByStatus(TicketStatus status) {
        return ticketRepository.findByTicketStatus(status)
                .stream()
                .map(ticketMapper::mapTicketToResponse)
                .toList();
    }


    public List<TicketResponse> getAllReservedTickets() {
        return getAllTicketsByStatus(TicketStatus.RESERVED);
    }

    public List<TicketResponse> getAllCancelledTickets() {
        return getAllTicketsByStatus(TicketStatus.CANCELLED);
    }

    public List<TicketResponse> getAllPaidTickets() {
        return getAllTicketsByStatus(TicketStatus.PAID);
    }

    // ✅ Tüm biletleri getir
    public List<TicketResponse> getAllTickets(Long userId) {
        User user = ticketHelper.getUserOrThrow(userId);
        return ticketRepository.findByUser(user)
                .stream()
                .map(ticketMapper::mapTicketToResponse)
                .toList();
    }


    // ✅ RESERVED biletleri getir
    public List<TicketResponse> getReservedTickets(Long userId) {
        return ticketHelper.getTicketsByStatus(userId, TicketStatus.RESERVED);
    }

    // ✅ CANCELLED biletleri getir
    public List<TicketResponse> getCancelledTickets(Long userId) {
        return ticketHelper.getTicketsByStatus(userId, TicketStatus.CANCELLED);
    }

    // ✅ PAID biletleri getir
    public List<TicketResponse> getPaidTickets(Long userId) {
        return ticketHelper.getTicketsByStatus(userId, TicketStatus.PAID);
    }





}
