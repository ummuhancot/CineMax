package com.cinemax.scheduler;

import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.entity.enums.MovieStatus;
import com.cinemax.repository.businnes.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class MovieComingSoonScheduler {

    private final MovieRepository movieRepository;

    // COMING_SOON filmlerin vizyona giriş tarihlerini tutacak map
    private final Map<Long, LocalDateTime> releaseDates = new ConcurrentHashMap<>();

    // -----------------------------
    // COMING_SOON filmleri schedule et
    // -----------------------------
    public void scheduleComingSoonMovie(Movie movie) {
        if (movie.getReleaseDate() != null) {
            releaseDates.put(movie.getId(), movie.getReleaseDate().atStartOfDay());
        }
    }

    // -----------------------------
    // Her 1 saatte bir kontrol et (projeye göre ayarlanabilir)
    // -----------------------------
    @Scheduled(fixedRateString = "${cinemax.movie.comingsoon.check-ms:3600000}") // 1 saat
    @Transactional
    public void checkAndStartMovies() {
        List<Movie> comingSoonMovies = movieRepository.findAllByStatus(MovieStatus.COMING_SOON);

        for (Movie movie : comingSoonMovies) {
            if (movie.getReleaseDate() == null) continue;

            LocalDateTime startDate = releaseDates.getOrDefault(movie.getId(),
                    movie.getReleaseDate().atStartOfDay());

            if (LocalDateTime.now().isAfter(startDate)) {
                // COMING_SOON → IN_THEATERS (opsiyonel hall ekleme için service çağırabilirsin)
                movie.setStatus(MovieStatus.IN_THEATERS);
                movieRepository.save(movie);

                // Map’ten temizle
                releaseDates.remove(movie.getId());
            }
        }
    }
}

