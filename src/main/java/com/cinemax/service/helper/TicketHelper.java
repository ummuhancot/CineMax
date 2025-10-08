package com.cinemax.service.helper;

import com.cinemax.entity.concretes.business.*;
import com.cinemax.entity.concretes.user.User;
import com.cinemax.entity.enums.PaymentStatus;
import com.cinemax.entity.enums.TicketStatus;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.repository.businnes.*;
import com.cinemax.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.cinemax.exception.InvalidRequestException;
import com.cinemax.payload.messages.ErrorMessages;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TicketHelper {

    private final UserRepository userRepository;
    private final HallRepository hallRepository;
    private final ShowTimeRepository showTimeRepository;
    private final PaymentRepository paymentRepository;
    private final TicketRepository ticketRepository;

    public User getUserOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.USER_NOT_FOUND));
    }

    public Hall getHallOrThrow(Long hallId) {
        return hallRepository.findById(hallId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.HALL_NOT_FOUND));
    }

    public ShowTime getShowTimeOrThrow(Long showTimeId) {
        return showTimeRepository.findById(showTimeId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.SHOWTIME_NOT_FOUND));
    }

    public Payment getPaymentOrThrow(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new InvalidRequestException(ErrorMessages.PAYMENT_NOT_FOUND));
    }

    public TicketStatus getTicketStatusFromPayment(Long paymentId) {
        if (paymentId == null) return TicketStatus.RESERVED;

        Payment payment = getPaymentOrThrow(paymentId); // repository veya helper metoduyla al
        PaymentStatus status = payment.getPaymentStatus();

        if (status == null) {
            throw new InvalidRequestException(ErrorMessages.PAYMENT_STATUS_NULL);
        }
        if (status == PaymentStatus.FAILED) {
            throw new InvalidRequestException(ErrorMessages.PAYMENT_FAILED);
        }

        return mapPaymentStatusToTicketStatus(status);
    }


    public static TicketStatus mapPaymentStatusToTicketStatus(PaymentStatus paymentStatus) {
        if (paymentStatus == null) return TicketStatus.RESERVED;

        return switch (paymentStatus) {
            case SUCCESS -> TicketStatus.PAID;
            case FAILED -> TicketStatus.CANCELLED;
            case PENDING -> TicketStatus.RESERVED;
        };
    }

    // Koltuk müsait mi kontrol et
    public void checkSeatAvailability(Long hallId, Long showTimeId, String seatLetter, int seatNumber) {
        boolean isTaken = ticketRepository.existsByHallIdAndShowtimeIdAndSeatLetterAndSeatNumberAndTicketStatusIn(
                hallId, showTimeId, seatLetter, seatNumber, List.of(TicketStatus.RESERVED, TicketStatus.PAID)
        );
        if (isTaken) {
            throw new InvalidRequestException(ErrorMessages.SEAT_ALREADY_RESERVED);
        }
    }

    // Price doğrulama
    public void validateTicketPrice(Double requestPrice, Double actualPrice) {
        if (!requestPrice.equals(actualPrice)) {
            throw new InvalidRequestException(
                    "Price mismatch: requested " + requestPrice + ", actual " + actualPrice
            );
        }
    }

    @Scheduled(fixedRate = 18000000) // her 1 dakikada bir kontrol eder
    @Transactional
    public void releaseExpiredReservations() {
        LocalDateTime now = LocalDateTime.now();

        // RESERVED ve süresi dolmuş rezervasyonları bul
        List<Ticket> expiredTickets = ticketRepository.findByTicketStatusAndExpiresAtBefore(
                TicketStatus.RESERVED, now
        );

        for (Ticket ticket : expiredTickets) {
            ticket.setTicketStatus(TicketStatus.CANCELLED); // rezervasyonu iptal et
            ticket.setExpiresAt(null); // süresini temizle
            ticketRepository.save(ticket); // koltuk tekrar alınabilir
        }

    }
}
