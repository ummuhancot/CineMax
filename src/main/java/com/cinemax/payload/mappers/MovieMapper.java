package com.cinemax.payload.mappers;

import com.cinemax.entity.concretes.business.Hall;
import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.payload.request.business.MovieRequest;
import com.cinemax.payload.response.business.MovieResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MovieMapper {

    /**
     * MovieRequest → Movie entity
     * Halls ve Poster entity service tarafında set edilecek
     */
    public Movie mapMovieRequestToMovie(MovieRequest request) {
        if (request == null) return null;

        return Movie.builder()
                .title(request.getTitle())
                .slug(request.getSlug()) // service tarafında otomatik üretiliyor
                .summary(request.getSummary())
                .releaseDate(request.getReleaseDate())
                .duration(request.getDuration())
                .rating(request.getRating())
                .director(request.getDirector())
                .genre(request.getGenre())
                .cast(request.getCast())
                .formats(request.getFormats())
                .status(request.getStatus() != null ? request.getStatus() : null) // service tarafında default atanacak
                .build();
    }

    /**
     * Movie entity → MovieResponse DTO
     */
    public MovieResponse mapMovieToMovieResponse(Movie movie) {
        if (movie == null) return null;

        List<Long> hallIds = null;
        if (movie.getHalls() != null) {
            hallIds = movie.getHalls().stream()
                    .map(Hall::getId)
                    .collect(Collectors.toList());
        }

        Long posterId = movie.getPoster() != null ? movie.getPoster().getId() : null;

        return MovieResponse.builder()
                .title(movie.getTitle())
                .slug(movie.getSlug())
                .summary(movie.getSummary())
                .releaseDate(movie.getReleaseDate())
                .duration(movie.getDuration())
                .rating(movie.getRating())
                .director(movie.getDirector())
                .genre(movie.getGenre())
                .cast(movie.getCast())
                .formats(movie.getFormats())
                .hallIds(hallIds)
                .posterId(posterId)
                .status(movie.getStatus())
                .build();
    }


    /**
     * Title'dan URL dostu slug üretir.
     * Örn: "The Dark Knight" → "the-dark-knight"
     */
    public static String generateSlug(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or empty for slug generation");
        }
        return title.toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9]+", "-")  // boşluk ve özel karakterleri '-' ile değiştir
                .replaceAll("^-|-$", "");       // baştaki ve sondaki '-' karakterlerini temizle
    }
}

