package com.cinemax.payload.mappers;

import com.cinemax.entity.concretes.business.Hall;
import com.cinemax.entity.concretes.business.Image;
import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.entity.enums.MovieStatus;
import com.cinemax.payload.request.business.MovieRequest;
import com.cinemax.payload.response.business.MovieResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MovieMapper {

    private final ShowTimeMapper showTimeMapper;
    private final ImageMapper imageMapper;

    /**
     * MovieRequest → Movie
     */
    public Movie mapMovieRequestToMovie(MovieRequest request, List<Hall> halls, String slug) {
        if (request.getSpecialHalls() != null && !request.getSpecialHalls().isEmpty()) {
            for (Hall hall : halls) {
                hall.setIsSpecial(request.getSpecialHalls().contains(hall.getType().name()));
            }
        }

        return Movie.builder()
                .title(request.getTitle())
                .slug(slug)
                .summary(request.getSummary())
                .releaseDate(request.getReleaseDate())
                .rating(request.getRating())
                .durationDays(request.getDurationDays() != null ? request.getDurationDays() : 30)
                .specialHalls(halls != null ?
                        halls.stream()
                                .filter(Hall::getIsSpecial)
                                .map(h -> h.getType().name())
                                .collect(Collectors.joining(", "))
                        : null)
                .director(request.getDirector())
                .cast(request.getCast() != null ? new ArrayList<>(request.getCast()) : new ArrayList<>())
                .formats(request.getFormats() != null ? new ArrayList<>(request.getFormats()) : new ArrayList<>())
                .genre(request.getGenre())
                .status(request.getStatus() != null ? request.getStatus() : MovieStatus.COMING_SOON)
                .halls(halls != null ? new ArrayList<>(halls) : new ArrayList<>())
                .build();
    }

    /**
     * 🔹 Yeni eklenen overload:
     * Testler ve eski kodlar bu imzayı kullanabiliyor.
     */
    public MovieResponse mapMovieToMovieResponse(Movie movie) {
        // Geriye dönük uyumluluk
        return mapMovieToMovieResponse(movie, List.of());
    }

    /**
     * Movie → MovieResponse
     */
    public MovieResponse mapMovieToMovieResponse(Movie movie, List<Image> images) {
        if (movie == null) return null;
        if (images == null) images = List.of();

        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .slug(movie.getSlug())
                .summary(movie.getSummary())
                .duration(movie.getDurationDays())
                .releaseDate(movie.getReleaseDate())
                .rating(movie.getRating())
                .cast(movie.getCast() != null ? new ArrayList<>(movie.getCast()) : new ArrayList<>())
                .formats(movie.getFormats() != null ? new ArrayList<>(movie.getFormats()) : new ArrayList<>())
                .genre(movie.getGenre())
                .status(movie.getStatus())
                .halls(movie.getHalls() != null ? movie.getHalls().stream()
                        .map(Hall::getName)
                        .collect(Collectors.toList()) : new ArrayList<>())
                .specialHalls(movie.getHalls() != null ? movie.getHalls().stream()
                        .filter(Hall::getIsSpecial)
                        .map(h -> h.getType().name() + ":" + h.getId())
                        .collect(Collectors.toList())
                        : new ArrayList<>())
                .images(images.stream()
                        .map(imageMapper::toImageMovieResponse)
                        .toList())
                .build();
    }

    /**
     * Movie güncelleme
     */
    public void updateMovieFromRequest(Movie movie, MovieRequest request, List<Hall> halls, Image poster) {
        if (movie == null || request == null) return;

        movie.setTitle(request.getTitle());
        movie.setSlug(request.getSlug());
        movie.setSummary(request.getSummary());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setRating(request.getRating());
        movie.setDirector(request.getDirector());
        movie.setGenre(request.getGenre());

        if (request.getStatus() != null) {
            movie.setStatus(request.getStatus());
        }

        movie.setCast(request.getCast() != null ? new ArrayList<>(request.getCast()) : new ArrayList<>());
        movie.setFormats(request.getFormats() != null ? new ArrayList<>(request.getFormats()) : new ArrayList<>());

        if (request.getDurationDays() != null) {
            movie.setDurationDays(request.getDurationDays());
        }

        if (halls != null && !halls.isEmpty()) {
            movie.setHalls(new ArrayList<>(halls));
            String specialHalls = halls.stream()
                    .filter(h -> Boolean.TRUE.equals(h.getIsSpecial()) && h.getId() != null)
                    .map(h -> h.getId().toString())
                    .collect(Collectors.joining(", "));
            movie.setSpecialHalls(specialHalls);
        } else {
            movie.setHalls(new ArrayList<>());
            movie.setSpecialHalls(null);
        }
    }
}
