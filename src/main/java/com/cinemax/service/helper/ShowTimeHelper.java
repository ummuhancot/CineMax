package com.cinemax.service.helper;

import com.cinemax.entity.concretes.business.Hall;
import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.entity.concretes.business.ShowTime;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.messages.ErrorMessages;
import com.cinemax.repository.businnes.HallRepository;
import com.cinemax.repository.businnes.MovieRepository;
import com.cinemax.repository.businnes.ShowTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ShowTimeHelper {

    private final MovieRepository movieRepository;
    private final HallRepository hallRepository;
    private final ShowTimeRepository showTimeRepository;

    public Movie getMovieOrThrow(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movieId));
    }

    public Hall getHallOrThrow(Long hallId) {
        return hallRepository.findById(hallId)
                .orElseThrow(() -> new ResourceNotFoundException("Hall not found with id: " + hallId));
    }

    /**
     * ID ile ShowTime bulur, yoksa exception fırlatır.
     */
    public ShowTime getShowTimeOrThrow(Long id) {
        return showTimeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ShowTime not found with id: " + id));
    }

}
