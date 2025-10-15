package com.cinemax.util;

import com.cinemax.entity.concretes.business.Hall;
import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.entity.enums.MovieStatus;

import com.cinemax.repository.businnes.MovieRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieStatusScheduler {

    private final MovieRepository movieRepository;

    // Her gün kontrol et
    @Scheduled(fixedRate = 24L * 60 * 60 * 1000)
    @Transactional
    public void finishMoviesAndRemoveFromHalls() {

        LocalDate today = LocalDate.now();

        // Sadece vizyondaki filmleri al
        List<Movie> inTheatersMovies = movieRepository.findAllByStatus(MovieStatus.IN_THEATERS);

        for (Movie movie : inTheatersMovies) {
            if (movie.getReleaseDate() == null) {
                continue;
            }

            // durationDays al, yoksa default 30 gün
            int durationDays = movie.getDurationDays() != null ? movie.getDurationDays() : 30;
            LocalDate lastDay = movie.getReleaseDate().plusDays(durationDays);

            if (today.isAfter(lastDay)) {
                // Filmi FINISHED yap
                movie.setStatus(MovieStatus.FINISHED);

                // Hall ilişkilerini kaldır
                if (movie.getHalls() != null && !movie.getHalls().isEmpty()) {
                    for (Hall hall : movie.getHalls()) {
                        hall.getMovies().remove(movie); // hall entity’sinden sil
                    }
                    movie.getHalls().clear(); // movie entity’sinden referansı temizle
                }
            }
        }

        // Yalnızca IN_THEATERS olanları kaydet
        movieRepository.saveAll(inTheatersMovies);
    }
}
