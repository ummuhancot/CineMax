package com.cinemax.repository.businnes;


import com.cinemax.entity.concretes.business.Ticket;
import com.cinemax.entity.concretes.user.User;
import com.cinemax.entity.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    List<Ticket> findByTicketStatus(TicketStatus ticketStatus);

    List<Ticket> findByUserAndTicketStatus(User user, TicketStatus status);

    List<Ticket> findByUser(User user);

    // Belirli salon ve seans için birden fazla status’a sahip biletleri getir
    List<Ticket> findByHallIdAndShowtimeIdAndTicketStatusIn(
            Long hallId,
            Long showTimeId,
            List<TicketStatus> statuses
    );

}

