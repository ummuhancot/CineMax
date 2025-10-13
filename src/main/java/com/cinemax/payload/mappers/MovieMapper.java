package com.cinemax.payload.mappers;

import com.cinemax.entity.concretes.business.Hall;
import com.cinemax.entity.concretes.business.Image;
import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.entity.enums.MovieStatus;
import com.cinemax.payload.request.business.MovieRequest;
import com.cinemax.payload.response.business.MovieResponse;
import com.cinemax.repository.businnes.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MovieMapper {

    private final ShowTimeMapper showTimeMapper;

    /**
     * Title'dan URL dostu slug üretir.
     * Örn: "The Dark Knight" → "the-dark-knight"
     */



    /**
     * MovieRequest → Movie
     * Poster burada opsiyonel, null olabilir.
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
                .duration(request.getDuration())
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
     * Movie → MovieResponse
     * Poster opsiyonel, null olabilir.
     */
    public MovieResponse mapMovieToMovieResponse(Movie movie) {
        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .slug(movie.getSlug())
                .summary(movie.getSummary())
                .releaseDate(movie.getReleaseDate())
                .duration(movie.getDuration())
                .rating(movie.getRating())
                .posterId(movie.getPoster() != null ? movie.getPoster().getId() : null)
                .posterUrl(movie.getPoster() != null ? movie.getPoster().getName() : null)
                .director(movie.getDirector())
                .cast(movie.getCast() != null ? new ArrayList<>(movie.getCast()) : new ArrayList<>())
                .formats(movie.getFormats() != null ? new ArrayList<>(movie.getFormats()) : new ArrayList<>())
                .genre(movie.getGenre())
                .status(movie.getStatus())
                .halls(movie.getHalls() != null ? movie.getHalls().stream()
                        .map(Hall::getName)
                        .collect(Collectors.toList()) : new ArrayList<>())
                .specialHalls(movie.getHalls() != null ? movie.getHalls().stream()
                        .filter(Hall::getIsSpecial)                   // sadece special olanları al
                        .map(h -> h.getType().name() + ":" + h.getId()) // örn: "VIP:15"
                        .collect(Collectors.toList())
                        : new ArrayList<>())
                .showTimes(movie.getShowTimes() != null ? movie.getShowTimes().stream()
                        .map(st -> st.getStartTime() + " - " + st.getEndTime())
                        .collect(Collectors.toList()) : new ArrayList<>())
                .build();
    }

    /**
     * Movie güncelleme
     * Poster opsiyonel, yalnızca poster entity verilirse set edilir.
     */
    public void updateMovieFromRequest(Movie movie, MovieRequest request, List<Hall> halls, Image poster) {
        if (movie == null || request == null) return;

        // Temel alanlar
        movie.setTitle(request.getTitle());
        movie.setSlug(request.getSlug());
        movie.setSummary(request.getSummary());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setDuration(request.getDuration());
        movie.setRating(request.getRating());
        movie.setDirector(request.getDirector());
        movie.setGenre(request.getGenre());

        // Status opsiyonel
        if (request.getStatus() != null) {
            movie.setStatus(request.getStatus());
        }

        // Poster opsiyonel
        if (poster != null) {
            movie.setPoster(poster);
        }

        // Cast ve Formats (null olabilir)
        movie.setCast(request.getCast() != null ? new ArrayList<>(request.getCast()) : new ArrayList<>());
        movie.setFormats(request.getFormats() != null ? new ArrayList<>(request.getFormats()) : new ArrayList<>());

        // Halls ve specialHalls
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
