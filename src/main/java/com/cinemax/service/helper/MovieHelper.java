package com.cinemax.service.helper;

import com.cinemax.entity.concretes.business.Hall;
import com.cinemax.entity.concretes.business.Image;
import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.exception.ImageException;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.messages.ErrorMessages;
import com.cinemax.repository.businnes.HallRepository;
import com.cinemax.repository.businnes.ImageRepository;
import com.cinemax.repository.businnes.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MovieHelper {

    private final HallRepository hallRepository;
    private final MovieRepository movieRepository;
    private final ImageRepository imageRepository;

    /**
     * Hall ID listesine göre salonları getirir.
     * Eğer hiçbir salon bulunamazsa ResourceNotFoundException fırlatır.
     */


    public List<Hall> getHallsOrThrow(List<Long> hallIds) {
        if (hallIds == null || hallIds.isEmpty()) return Collections.emptyList();

        List<Hall> halls = hallRepository.findAllById(hallIds);
        if (halls.isEmpty())
            throw new ResourceNotFoundException(ErrorMessages.HALL_NOT_FOUND);
        return halls;
    }


    public Movie getMovieOrThrow(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movieId));
    }


    public Image getPosterOrThrow(Long posterId) {
        if (posterId == null) {
            throw new ImageException(ErrorMessages.MOVIE_POSTER_REQUIRED);
        }

        return imageRepository.findById(posterId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.POSTER_NOT_FOUND));
    }
}
