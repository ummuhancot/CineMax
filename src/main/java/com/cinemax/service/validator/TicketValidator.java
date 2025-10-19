package com.cinemax.service.validator;

import com.cinemax.entity.concretes.business.Hall;
import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.entity.concretes.business.ShowTime;
import com.cinemax.entity.enums.TicketStatus;
import com.cinemax.exception.InvalidShowtimeRelationException;
import com.cinemax.exception.SeatAlreadyReservedException;
import com.cinemax.payload.messages.ErrorMessages;
import com.cinemax.repository.businnes.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class TicketValidator {

    private final TicketRepository ticketRepository;

    /**
     * Showtime, Movie ve Hall uyumluluğunu doğrular.
     * Kontrol edilenler:
     * 1️ Gönderilen showtime gerçekten belirtilen movie ile ilişkili mi?
     * 2️ Gönderilen showtime gerçekten belirtilen hall ile ilişkili mi?
     * Eğer herhangi biri uyumsuz ise InvalidShowtimeRelationException fırlatılır.
     * Bu sayede yanlış kombinasyonlarla rezervasyon yapılması engellenir.
     *
     * @param showtime kontrol edilecek ShowTime entity'si
     * @param movie kontrol edilecek Movie entity'si
     * @param hall kontrol edilecek Hall entity'si
     */
    public void validateShowtimeConsistency(ShowTime showtime, Movie movie, Hall hall) {
        // 🎬 Movie kontrolü
        if (!showtime.getMovie().getId().equals(movie.getId())) {
            throw new InvalidShowtimeRelationException(String.format(
                    ErrorMessages.SHOWTIME_MOVIE_MISMATCH,
                    showtime.getId(), movie.getId(), showtime.getMovie().getId()
            ));
        }

        // 🏛️ Hall kontrolü
        if (!showtime.getHall().getId().equals(hall.getId())) {
            throw new InvalidShowtimeRelationException(String.format(
                    ErrorMessages.SHOWTIME_HALL_MISMATCH,
                    showtime.getId(), hall.getId(), showtime.getHall().getId()
            ));
        }
    }



    public void validateSeatAvailability(Long showtimeId, String seatLetter, int seatNumber) {
        // Eğer RESERVED veya PAID ise doludur
        boolean occupied = ticketRepository.existsByShowtimeIdAndSeatLetterAndSeatNumberAndTicketStatusIn(
                showtimeId, seatLetter, seatNumber, Arrays.asList(TicketStatus.RESERVED, TicketStatus.PAID)
        );

        // Eğer varsa fakat expiresAt geçmişse, onu dolu saymıyoruz — bu cleanup scheduler ile temizlenecek.
        if (occupied) throw new SeatAlreadyReservedException("Bu koltuk zaten rezerve edilmiş veya satılmış!");
    }

    /**
     * Belirtilen koltuğun dolu olup olmadığını kontrol eder.
     * RESERVED veya PAID durumundaki biletleri dolu kabul eder.
     * expiresAt süresi geçmiş olan rezervasyonları boş sayar.
     *
     * @param showtimeId  Seans ID
     * @param hallId      Salon ID
     * @param seatLetter  Koltuk harfi (ör: A)
     * @param seatNumber  Koltuk numarası (ör: 5)
     */
    public void validateSeatAvailability(Long showtimeId, Long hallId, String seatLetter, int seatNumber) {
        boolean occupied = ticketRepository.existsByShowtimeIdAndHallIdAndSeatLetterAndSeatNumberAndTicketStatusInAndExpiresAtAfter(
                showtimeId,
                hallId,
                seatLetter,
                seatNumber,
                Arrays.asList(TicketStatus.RESERVED, TicketStatus.PAID),
                LocalDateTime.now()
        );

        if (occupied) {
            throw new SeatAlreadyReservedException(String.format(
                    "Koltuk zaten rezerve edilmiş veya satılmış: %s%d (Showtime ID: %d, Hall ID: %d)",
                    seatLetter, seatNumber, showtimeId, hallId
            ));
        }
    }
}
