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

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketHelper ticketHelper;
    private final TicketMapper ticketMapper;

    private static final int DEFAULT_RESERVATION_HOURS = 5; // 5 saatlik rezervasyon


    @Transactional
    public TicketResponse reserveTicket(TicketRequest request, String userEmail) {

        // 🔹 User, Hall, ShowTime al
        User user = ticketHelper.getUserOrThrow(userEmail);
        Hall hall = ticketHelper.getHallOrThrow(request.getHallId());
        ShowTime showTime = ticketHelper.getShowTimeOrThrow(request.getShowTimeId());

        // 🔹 Koltuk müsait mi kontrol et
        ticketHelper.checkSeatAvailability(
                hall.getId(),
                showTime.getId(),
                request.getSeatLetter(),
                request.getSeatNumber()
        );

        // 🔹 Payment ve TicketStatus
        TicketStatus status;
        Payment payment = null;

        if (request.getPaymentId() != null) {
            payment = ticketHelper.getPaymentOrThrow(request.getPaymentId());

            if (payment.getPaymentStatus() == null) {
                throw new InvalidRequestException(ErrorMessages.PAYMENT_STATUS_NULL);
            }

            if (payment.getPaymentStatus().equals(com.cinemax.entity.enums.PaymentStatus.FAILED)) {
                throw new InvalidRequestException(ErrorMessages.PAYMENT_FAILED);
            }
        }

        status = ticketHelper.getTicketStatusFromPayment(request.getPaymentId());

        // 🔹 Price doğrulama (sadece Ticket üzerinden)
        ticketHelper.validateTicketPrice(request.getPrice(), request.getPrice());
        // Eğer actualPrice farklı bir yerden geliyorsa burayı değiştir

        // 🔹 Ticket oluştur
        Ticket ticket = Ticket.builder()
                .seatLetter(request.getSeatLetter())
                .seatNumber(request.getSeatNumber())
                .price(request.getPrice()) // sadece Ticket üzerinden fiyat al
                .ticketStatus(status)
                .movie(showTime.getMovie())
                .hall(hall)
                .showtime(showTime)
                .payment(payment)
                .user(user)
                .expiresAt(LocalDateTime.now().plusHours(DEFAULT_RESERVATION_HOURS))//plusSeconds sn cinsinden bakar
                .build();

        ticketRepository.save(ticket);

        // 🔹 Mapper ile response döndür
        return ticketMapper.mapTicketToResponse(ticket);
    }









}
