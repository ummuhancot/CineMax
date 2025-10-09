package com.cinemax.payload.mappers;

import com.cinemax.entity.concretes.business.Hall;
import com.cinemax.entity.concretes.business.Image;
import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.entity.concretes.business.ShowTime;
import com.cinemax.entity.enums.MovieStatus;
import com.cinemax.payload.request.business.MovieRequest;
import com.cinemax.payload.response.business.MovieResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
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
    public static String generateSlug(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or empty for slug generation");
        }
        return title.toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9]+", "-")  // boşluk ve özel karakterleri '-' ile değiştir
                .replaceAll("^-|-$", "");       // baştaki ve sondaki '-' karakterlerini temizle
    }
    public Movie mapMovieRequestToMovie(MovieRequest request, List<Hall> halls) {
        Movie movie = Movie.builder()
                .title(request.getTitle())
                .slug(request.getSlug() != null ? request.getSlug() : generateSlug(request.getTitle()))
                .summary(request.getSummary())
                .releaseDate(request.getReleaseDate())
                .duration(request.getDuration())
                .rating(request.getRating())
                .specialHalls(halls != null ?
                        halls.stream()
                                .filter(Hall::getIsSpecial)
                                .map(h -> h.getId().toString())
                                .collect(Collectors.joining(", "))
                        : null)
                .director(request.getDirector())
                .cast(request.getCast() != null ? new ArrayList<>(request.getCast()) : new ArrayList<>())
                .formats(request.getFormats() != null ? new ArrayList<>(request.getFormats()) : new ArrayList<>())
                .genre(request.getGenre())
                .status(request.getStatus() != null ? request.getStatus() : MovieStatus.COMING_SOON)
                .halls(halls != null ? new ArrayList<>(halls) : new ArrayList<>())
                .build();

        // ShowTime ekleme
        if (request.getShowTimes() != null && !request.getShowTimes().isEmpty()) {
            List<ShowTime> showtimes = request.getShowTimes().stream()
                    .map(stReq -> showTimeMapper.toEntity(stReq, movie,
                            halls != null ? halls.stream()
                                    .filter(h -> h.getId().equals(stReq.getHallId()))
                                    .findFirst()
                                    .orElse(null)
                                    : null))
                    .toList();
            movie.setShowTimes(showtimes);
        }

        return movie;
    }




    // Movie → MovieResponse (response)
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
                .director(movie.getDirector())

                // List alanlarını güvenli (mutable) hale getirdik
                .cast(movie.getCast() != null ? new ArrayList<>(movie.getCast()) : new ArrayList<>())
                .formats(movie.getFormats() != null ? new ArrayList<>(movie.getFormats()) : new ArrayList<>())

                .genre(movie.getGenre())
                .status(movie.getStatus())
                .posterUrl(movie.getPoster() != null ? movie.getPoster().getName() : null)

                // Hall isimlerini listeliyoruz
                .halls(movie.getHalls() != null ? movie.getHalls().stream()
                        .map(Hall::getName)
                        .collect(Collectors.toCollection(ArrayList::new)) : new ArrayList<>())

                // ShowTime bilgilerini listeliyoruz
                .showTimes(movie.getShowTimes() != null ? movie.getShowTimes().stream()
                        .map(st -> st.getStartTime() + " - " + st.getEndTime())
                        .collect(Collectors.toCollection(ArrayList::new)) : new ArrayList<>())
                .build();
    }

    public void updateMovieFromRequest(Movie movie, MovieRequest request, List<Hall> halls, Image poster) {
        if (movie == null || request == null) return;

        // Temel alanlar
        movie.setTitle(request.getTitle());
        movie.setSlug(request.getSlug() != null ? request.getSlug() : generateSlug(request.getTitle()));
        movie.setSummary(request.getSummary());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setDuration(request.getDuration());
        movie.setRating(request.getRating());
        movie.setDirector(request.getDirector());
        movie.setGenre(request.getGenre());

        // Status opsiyonel
        movie.setStatus(request.getStatus() != null ? request.getStatus() : movie.getStatus());

        // Poster opsiyonel
        if (poster != null) {
            movie.setPoster(poster);
        }

        // Cast ve Formats mutable hale getiriyoruz
        movie.setCast(request.getCast() != null ? new ArrayList<>(request.getCast()) : new ArrayList<>());
        movie.setFormats(request.getFormats() != null ? new ArrayList<>(request.getFormats()) : new ArrayList<>());

        // Halls ve specialHalls mutable
        if (halls != null) {
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

        // ShowTimes ekleme veya güncelleme
        if (request.getShowTimes() != null && halls != null) {
            movie.getShowTimes().clear(); // önce mevcut listeyi temizle
            List<ShowTime> showtimes = request.getShowTimes().stream()
                    .map(stReq -> showTimeMapper.toEntity(
                            stReq,
                            movie,
                            halls.stream()
                                    .filter(h -> h.getId().equals(stReq.getHallId()))
                                    .findFirst()
                                    .orElse(null)
                    ))
                    .toList();
            movie.getShowTimes().addAll(showtimes); // ekle
        }

    }




}

