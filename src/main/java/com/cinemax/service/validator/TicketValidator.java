package com.cinemax.service.validator;

import com.cinemax.entity.enums.TicketStatus;
import com.cinemax.exception.SeatAlreadyReservedException;
import com.cinemax.repository.businnes.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class TicketValidator {

    private final TicketRepository ticketRepository;

    public void validateSeatAvailability(Long showtimeId, String seatLetter, int seatNumber) {
        // Eğer RESERVED veya PAID ise doludur
        boolean occupied = ticketRepository.existsByShowtimeIdAndSeatLetterAndSeatNumberAndTicketStatusIn(
                showtimeId, seatLetter, seatNumber, Arrays.asList(TicketStatus.RESERVED, TicketStatus.PAID)
        );

        // Eğer varsa fakat expiresAt geçmişse, onu dolu saymıyoruz — bu cleanup scheduler ile temizlenecek.
        if (occupied) throw new SeatAlreadyReservedException("Bu koltuk zaten rezerve edilmiş veya satılmış!");
    }

    public void ensureReservableState() {
        // placeholder for any global checks; örn gösterim iptal mi vb.
    }
}
