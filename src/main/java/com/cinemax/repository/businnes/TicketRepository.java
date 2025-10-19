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
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    boolean existsByShowtimeIdAndHallIdAndSeatLetterAndSeatNumberAndTicketStatusInAndExpiresAtAfter(
            Long showtimeId,
            Long hallId,
            String seatLetter,
            int seatNumber,
            List<TicketStatus> statuses,
            LocalDateTime currentTime
    );

    List<Ticket> findByTicketStatus(TicketStatus ticketStatus);

    List<Ticket> findByUserAndTicketStatus(User user, TicketStatus status);

    boolean existsByShowtimeIdAndSeatLetterAndSeatNumberAndTicketStatusIn(Long showtimeId, String seatLetter, int seatNumber, List<TicketStatus> statuses);

    List<Ticket> findAllByTicketStatusAndExpiresAtBefore(TicketStatus status, LocalDateTime before);

    Optional<Ticket> findById(Long id);
}

