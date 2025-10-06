package com.cinemax.payload.mappers;

import com.cinemax.entity.concretes.business.Hall;
import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.entity.enums.MovieStatus;
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
                .status(request.getStatus() != null ? request.getStatus() : MovieStatus.COMING_SOON) // burası eklendi
                .build();
    }

    /**
     * Movie entity → MovieResponse DTO
     */
    public MovieResponse mapMovieToMovieResponse(Movie movie) {
        if (movie == null) return null;

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
    public void updateMovieFromRequest(Movie movie, MovieRequest request) {
        if (movie == null || request == null) return;

        movie.setTitle(request.getTitle());
        movie.setSummary(request.getSummary());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setDuration(request.getDuration());
        movie.setRating(request.getRating());
        movie.setDirector(request.getDirector());
        movie.setGenre(request.getGenre());
        if (request.getStatus() != null) {
            movie.setStatus(request.getStatus());
        }
    }

}

