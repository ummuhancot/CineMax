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
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessages.USER_NOT_FOUND + userId));
    }

    public Movie getMovieOrThrow(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessages.MOVIE_NOT_FOUND + movieId));
    }

    public Hall getHallOrThrow(Long hallId) {
        return hallRepository.findById(hallId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessages.HALL_NOT_FOUND + hallId));
    }

    public ShowTime getShowTimeOrThrow(Long showTimeId) {
        return showTimeRepository.findById(showTimeId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessages.SHOWTIME_NOT_FOUND + showTimeId));
    }


    public Ticket getTicketOrThrow(Long ticketId){
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessages.TICKET_NOT_FOUND + ticketId));
    }
}
