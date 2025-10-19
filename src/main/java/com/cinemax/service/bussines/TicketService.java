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
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

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

        // 🎯 Showtime–Movie–Hall uyumluluk kontrolü
        ticketValidator.validateShowtimeConsistency(showtime, movie, hall);

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


    @Transactional
    public List<TicketResponse> reserveUserMultipleTickets(List<TicketRequest> requests) {
        List<TicketResponse> responses = new ArrayList<>();

        for (TicketRequest request : requests) {
            User user = ticketHelper.getUserOrThrow(request.getUserId());
            Movie movie = ticketHelper.getMovieOrThrow(request.getMovieId());
            Hall hall = ticketHelper.getHallOrThrow(request.getHallId());
            ShowTime showtime = ticketHelper.getShowTimeOrThrow(request.getShowtimeId());

            ticketValidator.validateShowtimeConsistency(showtime, movie, hall);

            // Koltuk müsaitlik kontrolü
            ticketValidator.validateSeatAvailability(
                    request.getShowtimeId(),
                    request.getHallId(),
                    request.getSeatLetter(),
                    request.getSeatNumber()
            );

            // DTO → Entity
            Ticket ticket = ticketMapper.toEntity(
                    request, user, movie, hall, showtime,
                    request.getDurationMinutes() != null ? request.getDurationMinutes() : defaultReservationMinutes
            );

            ticket.setTicketStatus(TicketStatus.RESERVED);

            ticket = ticketRepository.save(ticket);

            // Status ve expiresAt güncelle
            ticket = statusManager.setReserved(ticket,
                    request.getDurationMinutes() != null ? request.getDurationMinutes() : defaultReservationMinutes
            );

            // Bildirim gönder
            notificationService.sendReservationConfirmation(ticket);

            // Response ekle
            responses.add(ticketMapper.toResponse(ticket));
        }

        return responses;
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

    /**
     * Tüm biletleri getirir.
     */
    @Transactional(readOnly = true)
    public List<TicketResponse> getAllTickets() {
        return ticketRepository.findAll()
                .stream()
                .map(ticketMapper::toResponse)
                .toList();
    }

    /**
     * ID'ye göre tek bilet getirir.
     */
    @Transactional(readOnly = true)
    public TicketResponse getTicketById(Long id) {
        Ticket ticket = ticketHelper.getTicketOrThrow(id);
        return ticketMapper.toResponse(ticket);
    }

    /**
     * Statüsüne göre biletleri getirir.
     */
    @Transactional(readOnly = true)
    public List<TicketResponse> getTicketsByStatus(String status) {
        TicketStatus ticketStatus;
        try {
            ticketStatus = TicketStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Geçersiz ticket statüsü: " + status);
        }

        List<Ticket> tickets = ticketRepository.findByTicketStatus(ticketStatus);
        return tickets.stream()
                .map(ticketMapper::toResponse)
                .toList();
    }

    /**
     * Belirli bir kullanıcıya ve statüye (ör: RESERVED, PAID, CANCELLED) göre biletleri getirir.
     *
     * @param userId Kullanıcının ID bilgisi
     * @param status Bilet statüsü (TicketStatus enum değeri)
     * @return Belirtilen kullanıcıya ve statüye sahip biletlerin listesi
     */
    @Transactional(readOnly = true)
    public List<TicketResponse> getTicketsByUserAndStatus(Long userId, String status) {
        // 1️⃣ User kontrolü
        User user = ticketHelper.getUserOrThrow(userId);

        // 2️⃣ TicketStatus enum dönüşümü
        TicketStatus ticketStatus;
        try {
            ticketStatus = TicketStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Geçersiz ticket statüsü: " + status);
        }

        // 3️⃣ DB sorgusu
        List<Ticket> tickets = ticketRepository.findByUserAndTicketStatus(user, ticketStatus);

        // 4️⃣ Mapper dönüşü
        return tickets.stream()
                .map(ticketMapper::toResponse)
                .toList();
    }


    /**
     * Mevcut bir bileti günceller.
     * durationMinutes varsa expiresAt güncellenir.
     */
    @Transactional
    public TicketResponse updateTicket(Long ticketId, TicketRequest request) {

        // 1️⃣ Ticket bul
        Ticket ticket = ticketHelper.getTicketOrThrow(ticketId);

        // 2️⃣ İlgili entity’leri al
        ShowTime showTime = null;
        if (request.getShowtimeId() != null) showTime = ticketHelper.getShowTimeOrThrow(request.getShowtimeId());

        Hall hall = null;
        if (request.getHallId() != null) hall = ticketHelper.getHallOrThrow(request.getHallId());

        Movie movie = null;
        if (request.getMovieId() != null) movie = ticketHelper.getMovieOrThrow(request.getMovieId());


        // 3️⃣ Mapper ile güncelle
        ticket = ticketMapper.updateFromRequest(ticket, request, showTime, hall, movie);

        // 4️⃣ durationMinutes varsa expiresAt güncelle
        if (request.getDurationMinutes() != null) {
            ticket = statusManager.setReserved(ticket, request.getDurationMinutes());
        }

        // 5️⃣ DB’ye kaydet
        ticket = ticketRepository.save(ticket);

        // 6️⃣ DTO’ya çevir ve döndür
        return ticketMapper.toResponse(ticket);
    }


}
