package com.cinemax.repository.bussines;

import com.cinemax.entity.concretes.business.Ticket;
import com.cinemax.entity.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    boolean existsByShowTime_IdAndSeatNo(Long showTimeId, String seatNo);

    // (ileride T-1/T-2 için de kullanışlı)

    Page<Ticket> findByUser_IdAndStatusIn(Long userId, List<TicketStatus> statuses, Pageable pageable);
    Page<Ticket> findByUser_IdAndStatus(Long userId, TicketStatus status, Pageable pageable);
}
