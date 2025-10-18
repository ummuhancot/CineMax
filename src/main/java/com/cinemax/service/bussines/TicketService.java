package com.cinemax.service.bussines;

import com.cinemax.entity.concretes.business.*;
import com.cinemax.entity.concretes.user.User;
import com.cinemax.entity.enums.TicketStatus;
import com.cinemax.payload.mappers.TicketMapper;
import com.cinemax.payload.request.business.TicketRequest;
import com.cinemax.payload.response.business.TicketResponse;
import com.cinemax.repository.businnes.TicketRepository;
import com.cinemax.service.helper.TicketHelper;
import com.cinemax.service.statusmanager.TicketStatusManager;
import com.cinemax.service.validator.TicketValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;
    private final TicketValidator ticketValidator;
    private final TicketStatusManager statusManager;
    private final NotificationService notificationService;
    private final TicketHelper ticketHelper;

    private final int defaultReservationMinutes = 10;

    /**
     * Yeni bir bilet rezervasyonu oluşturur.
     * @param request TicketRequest DTO
     * @return Rezervasyon yapılmış Ticket entity
     */
    @Transactional
    public TicketResponse reserveTicket(TicketRequest request) {
        // Koltuk müsaitlik kontrolü
        ticketValidator.validateSeatAvailability(
                request.getShowtimeId(),
                request.getSeatLetter(),
                request.getSeatNumber()
        );

        User user = ticketHelper.getUserOrThrow(request.getUserId());
        Movie movie = ticketHelper.getMovieOrThrow(request.getMovieId());
        Hall hall = ticketHelper.getHallOrThrow(request.getHallId());
        ShowTime showtime = ticketHelper.getShowTimeOrThrow(request.getShowtimeId());
        // Mapper ile DTO → Entity
        Ticket ticket = ticketMapper.toEntity(request, user, movie, hall, showtime, defaultReservationMinutes);
        ticket.setTicketStatus(TicketStatus.RESERVED);  // 🔑 Ticket durumunu ayarla
        ticket = ticketRepository.save(ticket);

        // Status ve expiresAt güncelle
        ticket = statusManager.setReserved(ticket,
                request.getDurationMinutes() != null ? request.getDurationMinutes() : defaultReservationMinutes);

        // Bildirim gönder
        notificationService.sendReservationConfirmation(ticket);

        // ✅ TicketResponse döndür
        return ticketMapper.toResponse(ticket);
    }

    /**
     * Rezervasyonu iptal eder.
     * @param ticketId Bilet ID
     * @return İptal edilmiş Ticket entity
     */
    @Transactional
    public TicketResponse cancelReservation(Long ticketId) {
        Ticket ticket = ticketHelper.getTicketOrThrow(ticketId);

        ticket = statusManager.setCancelled(ticket);

        // ✅ Mapper ile TicketResponse döndür
        return ticketMapper.toResponse(ticket);
    }



}
