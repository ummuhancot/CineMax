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

        // Vizyondaki tüm filmleri al
        List<Movie> inTheatersMovies = movieRepository.findAllByStatus(MovieStatus.IN_THEATERS);

        for (Movie movie : inTheatersMovies) {
            if (movie.getReleaseDate() == null) {
                continue;
            }

            // 30 gün varsayılan
            int durationDays = movie.getDurationDays() != null ? movie.getDurationDays() : 30;
            LocalDate lastDay = movie.getReleaseDate().plusDays(durationDays - 1);

            if (today.isAfter(lastDay)) {
                // Film statusunu FINISHED yap
                movie.setStatus(MovieStatus.FINISHED);

                // Hall ilişkilerini kaldır
                if (movie.getHalls() != null && !movie.getHalls().isEmpty()) {
                    for (Hall hall : movie.getHalls()) {
                        hall.getMovies().remove(movie); // hall entity’sinden sil
                    }
                    movie.getHalls().clear(); // movie entity’sinden de referansı temizle
                }
            }
        }

        movieRepository.saveAll(inTheatersMovies);
    }


    @Scheduled(fixedRate = 20000)
    @Transactional
    public void finishMoviesAndRemoveFromHallsDemo() {

        // Vizyondaki tüm filmleri al
        List<Movie> inTheatersMovies = movieRepository.findAllByStatus(MovieStatus.IN_THEATERS);

        for (Movie movie : inTheatersMovies) {
            if (movie.getReleaseDate() == null) {
                continue;
            }

            // Normal kullanımda 30 gün
            int durationDays = movie.getDurationDays() != null ? movie.getDurationDays() : 30;

            // Demo amaçlı: 20 saniye geçtiğini varsay, tarih kontrolü yapma
            // Böylece hızlıca FINISHED ve salonlardan temizlenmiş olacak

            // 1️⃣ Status'u FINISHED yap
            movie.setStatus(MovieStatus.FINISHED);

            // 2️⃣ Hall ilişkilerini kaldır
            if (movie.getHalls() != null && !movie.getHalls().isEmpty()) {
                for (Hall hall : movie.getHalls()) {
                    hall.getMovies().remove(movie); // hall entity’sinden sil
                }
                movie.getHalls().clear(); // movie entity’sinden de referansı temizle
            }
        }

        movieRepository.saveAll(inTheatersMovies);
    }
}
