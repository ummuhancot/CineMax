package com.cinemax.payload.mappers;

import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.entity.concretes.business.ShowTime;
import com.cinemax.payload.response.business.MovieShowTimesResponse;
import com.cinemax.payload.response.business.ShowTimeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MovieShowTimesMapper {

    private final ShowTimeMapper showTimeMapper;

    public MovieShowTimesResponse mapMovieWithShowTimesToResponse(Movie movie, List<ShowTime> showTimes) {
        if (movie == null) return null;

        List<ShowTimeResponse> showTimeResponses = showTimes.stream()
                .map(showTimeMapper::mapShowTimeToResponse)
                .collect(Collectors.toList());

        MovieShowTimesResponse dto = new MovieShowTimesResponse();
        dto.setMovieId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setShowTimes(showTimeResponses);

        return dto;
    }
}
