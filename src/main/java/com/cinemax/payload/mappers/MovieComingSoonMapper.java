package com.cinemax.payload.mappers;

import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.entity.enums.MovieStatus;
import com.cinemax.payload.request.business.MovieComingSoonRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class MovieComingSoonMapper {



    /**
     * MovieComingSoonRequest'ten Movie entity'si oluşturur.
     */
    public Movie toEntity(MovieComingSoonRequest request) {
        return Movie.builder()
                .title(request.getTitle())
                .summary(request.getSummary())
                .releaseDate(request.getReleaseDate())
                .rating(request.getRating())
                .durationDays(request.getDurationDays() != null ? request.getDurationDays() : 10)
                .director(request.getDirector())
                .cast(request.getCast() != null ? new ArrayList<>(request.getCast()) : new ArrayList<>())
                .formats(request.getFormats() != null ? new ArrayList<>(request.getFormats()) : new ArrayList<>())
                .genre(request.getGenre())
                .status(request.getStatus() != null ? request.getStatus() : MovieStatus.COMING_SOON)
                .build();
    }


}
