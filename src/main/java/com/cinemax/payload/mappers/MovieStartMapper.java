package com.cinemax.payload.mappers;

import com.cinemax.entity.concretes.business.Hall;
import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.payload.request.business.MovieStartRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MovieStartMapper {

    public Movie mapMovieStartRequestToMovie(Movie movie, MovieStartRequest request, List<Hall> halls) {
        if (request.getSpecialHalls() != null && !request.getSpecialHalls().isEmpty()) {
            for (Hall hall : halls) {
                hall.setIsSpecial(request.getSpecialHalls().contains(hall.getType().name()));
            }
        }

        String specialHalls = halls.stream()
                .filter(Hall::getIsSpecial)
                .map(h -> h.getType().name())
                .collect(Collectors.joining(", "));

        movie.setHalls(new ArrayList<>(halls));
        movie.setSpecialHalls(specialHalls.isEmpty() ? null : specialHalls);
        movie.setDurationDays(request.getDurationDays() != null ? request.getDurationDays() : movie.getDurationDays());

        return movie;
    }

}
