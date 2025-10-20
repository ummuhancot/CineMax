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
public class MovieFinishScheduler {

    private final MovieRepository movieRepository;

    // Filmlerin finish tarihlerini tutacak map
    private final Map<Long, LocalDateTime> finishDates = new ConcurrentHashMap<>();

    // -----------------------------
    // Film finish zamanı schedule et
    // -----------------------------
    public void scheduleMovieFinish(Movie movie, LocalDateTime finishDate) {
        finishDates.put(movie.getId(), finishDate);
    }

    // -----------------------------
    // Her 1 saatte bir kontrol et (projeye göre ayarlanabilir)
    // -----------------------------
    @Scheduled(fixedRateString = "${cinemax.movie.finish.check-ms:3600000}") // 1 saat
    @Transactional
    public void checkAndFinishMovies() {
        // Vizyondaki filmleri al
        List<Movie> inTheatersMovies = movieRepository.findAllByStatus(MovieStatus.IN_THEATERS);

        for (Movie movie : inTheatersMovies) {
            if (movie.getReleaseDate() == null || movie.getDurationDays() == null) continue;

            // Öncelikle map’te varsa onun üzerinden finishDate al
            LocalDateTime finishDate = finishDates.getOrDefault(
                    movie.getId(),
                    movie.getReleaseDate().atStartOfDay().plusDays(movie.getDurationDays())
            );

            if (LocalDateTime.now().isAfter(finishDate)) {
                // Filmi FINISHED yap ve hall ilişkilerini temizle
                movie.setStatus(MovieStatus.FINISHED);
                if (movie.getHalls() != null) movie.getHalls().clear();
                movieRepository.save(movie);

                // Map’ten temizle
                finishDates.remove(movie.getId());
            }
        }
    }
}

