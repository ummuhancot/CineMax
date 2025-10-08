package com.cinemax.service.bussines;

import com.cinemax.entity.concretes.business.*;
import com.cinemax.entity.concretes.user.User;
import com.cinemax.entity.enums.PaymentStatus;
import com.cinemax.entity.enums.TicketStatus;
import com.cinemax.exception.InvalidRequestException;
import com.cinemax.payload.mappers.TicketMapper;
import com.cinemax.payload.messages.ErrorMessages;
import com.cinemax.payload.request.business.TicketRequest;
import com.cinemax.payload.response.business.TicketResponse;
import com.cinemax.repository.businnes.TicketRepository;
import com.cinemax.service.helper.HallHelper;
import com.cinemax.service.helper.ShowTimeHelper;
import com.cinemax.service.helper.TicketHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketHelper ticketHelper;
    private final TicketMapper ticketMapper;


    private static final int DEFAULT_RESERVATION_HOURS = 5; // 5 saatlik rezervasyon


    @Transactional
    public TicketResponse reserveTicket(TicketRequest request, String userEmail) {

        // 🔹 1. User, Hall, ShowTime al
        User user = ticketHelper.getUserOrThrow(userEmail);
        Hall hall = ticketHelper.getHallOrThrow(request.getHallId());
        ShowTime showTime = ticketHelper.getShowTimeOrThrow(request.getShowTimeId());

        // 🔹 2. Koltuk müsait mi kontrol et
        ticketHelper.checkSeatAvailability(
                hall.getId(),
                showTime.getId(),
                request.getSeatLetter(),
                request.getSeatNumber()
        );

        // 🔹 3. Payment kontrolü ve TicketStatus belirleme
        Payment payment = null;
        TicketStatus status;

        if (request.getPaymentId() != null) {
            payment = ticketHelper.getPaymentOrThrow(request.getPaymentId());

            if (payment.getPaymentStatus() == null) {
                throw new InvalidRequestException(ErrorMessages.PAYMENT_STATUS_NULL);
            }

            if (payment.getPaymentStatus().equals(PaymentStatus.FAILED)) {
                throw new InvalidRequestException(ErrorMessages.PAYMENT_FAILED);
            }
        }

        status = ticketHelper.getTicketStatusFromPayment(request.getPaymentId());

        // 🔹 4. Fiyat doğrulama
        // Burada ikinci parametre "beklenen fiyat" olabilir, şu anda aynı kullanılmış
        ticketHelper.validateTicketPrice(request.getPrice(), request.getPrice());

        // 🔹 5. Ticket oluştur
        Ticket ticket = Ticket.builder()
                .seatLetter(request.getSeatLetter())
                .seatNumber(request.getSeatNumber())
                .price(request.getPrice())
                .ticketStatus(status)
                .movie(showTime.getMovie())
                .hall(hall)
                .showtime(showTime)
                .payment(payment)
                .user(user)
                .expiresAt(LocalDateTime.now().plusHours(DEFAULT_RESERVATION_HOURS))
                .build();

        ticketRepository.save(ticket);
        return ticketMapper.mapTicketToResponse(ticket);
    }









}
