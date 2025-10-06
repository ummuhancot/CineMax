package com.cinemax.repository.businnes;


import com.cinemax.entity.concretes.business.Ticket;
import com.cinemax.entity.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {



    // Süresi dolmuş rezervasyonları bul
    List<Ticket> findByTicketStatusAndExpiresAtBefore(
            TicketStatus ticketStatus,
            LocalDateTime now
    );


    boolean existsByHallIdAndShowtimeIdAndSeatLetterAndSeatNumberAndTicketStatusIn(
            Long hallId,
            Long showTimeId,
            String seatLetter,
            int seatNumber,
            List<TicketStatus> statuses
    );


}

