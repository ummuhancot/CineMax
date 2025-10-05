package com.cinemax.service.bussines;



import com.cinemax.entity.concretes.business.ShowTime;
import com.cinemax.entity.concretes.business.Ticket;
import com.cinemax.entity.concretes.user.User;
import com.cinemax.entity.enums.TicketStatus;
import com.cinemax.exception.ConflictException;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.mappers.TicketMapper;
import com.cinemax.payload.request.business.TicketBuyRequest;
import com.cinemax.payload.response.business.TicketResponse;
import com.cinemax.repository.bussines.ShowTimeRepository;
import com.cinemax.repository.bussines.TicketRepository;
import com.cinemax.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ShowTimeRepository showTimeRepository;
    private final UserRepository userRepository;
    private final TicketMapper ticketMapper;

    private User getAuthUser(Principal principal) {
        String email = principal.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    /** T-4: bilet satın alma */
    public TicketResponse buy(TicketBuyRequest req, Principal principal) {
        User user = getAuthUser(principal);

        ShowTime showTime = showTimeRepository.findById(req.getShowTimeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ShowTime not found with id=" + req.getShowTimeId()));


        // Zaman kuralı: seans geçmişte olamaz
        LocalDateTime now = LocalDateTime.now();
        if (showTime.getStartTime() != null && !showTime.getStartTime().isAfter(now)) {
            throw new ConflictException("Showtime already started/finished");
        }

        // Koltuk çakışması
        if (ticketRepository.existsByShowTime_IdAndSeatNo(req.getShowTimeId(), req.getSeatNo())) {
            throw new ConflictException("Seat already reserved/sold");
        }

        Ticket t = new Ticket();
        t.setUser(user);
        t.setShowTime(showTime);
        t.setSeatNo(req.getSeatNo());
        t.setStatus(TicketStatus.SOLD);
        t.setReservedAt(LocalDateTime.now());

        return ticketMapper.toResponse(ticketRepository.save(t));
    }

    /**
     * T-3: GET /api/tickets/reserve/{id}?seatNo=...
     * - default: {id} = showTimeId
     * - useMovieId=true ise {id} = movieId, en yakın seans bulunur
     */
    public TicketResponse reserveByGet (Long id, String seatNo,boolean useMovieId, Principal principal){
        if (seatNo == null || seatNo.trim().isEmpty())
            throw new IllegalArgumentException("seatNo is required");

        User user = getAuthUser(principal);

        ShowTime target = !useMovieId
                ? showTimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ShowTime not found with id=" + id))
                : showTimeRepository.findNearestUpcomingByMovieId(id, LocalDateTime.now())
                .orElseThrow(() -> new ResourceNotFoundException("No upcoming showtime for movieId=" + id));

        if (ticketRepository.existsByShowTime_IdAndSeatNo(target.getId(), seatNo))
            throw new ConflictException("Seat already reserved/sold");

        Ticket t = new Ticket();
        t.setUser(user);
        t.setShowTime(target);
        t.setSeatNo(seatNo);
        t.setStatus(TicketStatus.RESERVED);
        t.setReservedAt(LocalDateTime.now());

        return ticketMapper.toResponse(ticketRepository.save(t));
    }
}
