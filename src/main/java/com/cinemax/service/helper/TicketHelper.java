package com.cinemax.service.helper;

import com.cinemax.entity.concretes.business.*;
import com.cinemax.entity.concretes.user.User;
import com.cinemax.entity.enums.PaymentStatus;
import com.cinemax.entity.enums.TicketStatus;
import com.cinemax.exception.InvalidRequestException;
import com.cinemax.exception.NoAvailableSeatsException;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.exception.SeatsNotLoadedException;
import com.cinemax.payload.mappers.TicketMapper;
import com.cinemax.payload.messages.ErrorMessages;
import com.cinemax.payload.response.business.TicketResponse;
import com.cinemax.repository.businnes.*;
import com.cinemax.repository.user.UserRepository;
import com.cinemax.util.HallSeatCache;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TicketHelper {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final HallRepository hallRepository;
    private final ShowTimeRepository showTimeRepository;
    private final PaymentRepository paymentRepository;
    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;

    // ------------------- Entity Getters -------------------
    public User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı. ID: " + userId));
    }

    public Movie getMovieOrThrow(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Film bulunamadı. ID: " + movieId));
    }

    public Hall getHallOrThrow(Long hallId) {
        return hallRepository.findById(hallId)
                .orElseThrow(() -> new IllegalArgumentException("Salon bulunamadı. ID: " + hallId));
    }

    public ShowTime getShowTimeOrThrow(Long showTimeId) {
        return showTimeRepository.findById(showTimeId)
                .orElseThrow(() -> new IllegalArgumentException("Seans bulunamadı. ID: " + showTimeId));
    }

    // ------------------- Payment / TicketStatus -------------------
//    public TicketStatus getTicketStatusFromPayment(Long paymentId) {
//        if (paymentId == null) return TicketStatus.RESERVED;
//
//        Payment payment = getPaymentOrThrow(paymentId);
//
//        if (payment.getPaymentStatus() == null) {
//            throw new InvalidRequestException(ErrorMessages.PAYMENT_STATUS_NULL);
//        }
//
//        if (payment.getPaymentStatus() == PaymentStatus.FAILED) {
//            throw new InvalidRequestException(ErrorMessages.PAYMENT_FAILED);
//        }
//
//        return mapPaymentStatusToTicketStatus(payment.getPaymentStatus());
//    }

    public TicketStatus mapPaymentStatusToTicketStatus(PaymentStatus paymentStatus) {
        if (paymentStatus == null) {return TicketStatus.EMPTY;}
        return switch (paymentStatus) {
            case SUCCESS -> TicketStatus.PAID;
            case FAILED -> TicketStatus.CANCELLED;
            case PENDING -> TicketStatus.RESERVED;
        };
    }



    // ------------------- Seat / Reservation -------------------
    public void checkSeatAvailability(Long hallId, Long showTimeId, String seatLetter, int seatNumber) {
        boolean taken = ticketRepository.existsByHallIdAndShowtimeIdAndSeatLetterAndSeatNumberAndTicketStatusIn(
                hallId, showTimeId, seatLetter, seatNumber, List.of(TicketStatus.RESERVED, TicketStatus.PAID)
        );
        if (taken) throw new InvalidRequestException(ErrorMessages.SEAT_ALREADY_RESERVED);
    }
    public String pickAvailableSeat(Long hallId, Long showTimeId) {
        // 1. Koltuk listesini al ve kontrol et
        List<String> seats = Optional.ofNullable(HallSeatCache.getSeats(hallId))
                .orElseThrow(() -> new SeatsNotLoadedException(ErrorMessages.SEATS_NOT_LOADED + hallId));

        // 2. Koltukları tek tek kontrol et
        for (String seat : seats) {
            Map<String, Object> parsed = parseSeat(seat);
            String letter = (String) parsed.get("seatLetter");
            int number = (Integer) parsed.get("seatNumber");

            boolean taken = ticketRepository.existsByHallIdAndShowtimeIdAndSeatLetterAndSeatNumberAndTicketStatusIn(
                    hallId, showTimeId, letter, number, List.of(TicketStatus.RESERVED, TicketStatus.PAID)
            );

            if (!taken) {
                return seat;
            }
        }

        // 3. Hiç koltuk boş değilse özel exception fırlat
        throw new NoAvailableSeatsException(ErrorMessages.NO_AVAILABLE_SEATS+hallId);
    }


    public Map<String, Object> parseSeat(String seat) {
        String[] parts = seat.split("-");
        if (parts.length != 2) throw new IllegalArgumentException(ErrorMessages.INVALID_SEAT_FORMAT + seat);
        Map<String, Object> result = new HashMap<>();
        result.put("seatLetter", parts[0]);
        result.put("seatNumber", Integer.parseInt(parts[1]));
        return result;
    }

    // ------------------- Expired Reservation Release -------------------
//    @Scheduled(fixedRate = 60000)
    @Scheduled(fixedRate = 5000) //5 saniyede bir kontrol etsin
    @Transactional
    public void releaseExpiredReservations() {
        LocalDateTime now = LocalDateTime.now();
        List<Ticket> expired = ticketRepository.findByTicketStatusAndExpiresAtBefore(TicketStatus.RESERVED, now);
        for (Ticket t : expired) {
            t.setTicketStatus(TicketStatus.CANCELLED);
            t.setExpiresAt(null);
            ticketRepository.save(t);
        }
    }

    public List<TicketResponse> getTicketsByStatus(Long userId, TicketStatus status) {
        User user = getUserOrThrow(userId);
        return ticketRepository.findByUserAndTicketStatus(user, status)
                .stream()
                .map(ticketMapper::mapTicketToResponse)
                .toList();
    }
}
