package com.cinemax.payload.mappers;

import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.payload.response.business.MovieAdminResponse;
import org.springframework.stereotype.Component;

@Component
public class MovieAdminMapper {

    public MovieAdminResponse toAdminResponse(Movie movie) {
        if (movie == null) return null;

        return MovieAdminResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .slug(movie.getSlug())
                .status(movie.getStatus())
                .releaseDate(movie.getReleaseDate())
                .build();
    }
}
