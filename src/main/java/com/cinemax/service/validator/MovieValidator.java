package com.cinemax.service.validator;

import com.cinemax.entity.concretes.business.Hall;
import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.entity.enums.MovieStatus;
import com.cinemax.payload.request.business.MovieRequest;
import com.cinemax.repository.businnes.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MovieValidator {

    private final MovieRepository movieRepository;

    public void validateUniqueMovieInHalls(String movieSlug, Hall hall) {
        boolean exists = movieRepository.existsByHallAndMovieSlug(
                hall.getId(),
                movieSlug
        );

        if (exists) {
            throw new IllegalArgumentException(
                    "Seçilen salon (" + hall.getName() + ") için aynı film zaten mevcut!"
            );
        }
    }

    public static String generateUniqueSlug(MovieRequest request, List<Hall> halls, MovieRepository movieRepository) {
        String cinemaName = halls.get(0).getCinema().getName();
        String hallName = halls.get(0).getName();

        return request.getSlug() != null
                ? normalizeSlug(cinemaName, hallName, request.getSlug())
                : normalizeSlug(cinemaName, hallName, request.getTitle());


    }


    // 🔹 Slug normalize methodu
    public static String normalizeSlug(String cinemaName, String hallName, String title) {
        String normalizedCinema = cinemaName.toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");

        String normalizedHall = hallName.toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");

        String normalizedTitle = title.toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");

        return normalizedCinema + "-" + normalizedHall + "-" + normalizedTitle;
    }


    /**
     * Salonlar ve film bilgisi üzerinden slug üretir ve DB'de kontrol eder.
     * Aynı slug varsa IllegalArgumentException fırlatır.
     */
    public static String generateUniqueSlugForTheaters(Movie movie, List<Hall> halls, MovieRepository movieRepository) {
        if (halls == null || halls.isEmpty()) {
            throw new IllegalArgumentException("En az bir salon seçilmelidir!");
        }

        // Salon ve cinema isimlerinden slug oluştur
        String cinemaName = halls.get(0).getCinema().getName();
        String hallName = halls.get(0).getName();
        String baseText = movie.getSlug() != null && !movie.getSlug().isBlank()
                ? movie.getSlug()
                : movie.getTitle();

        String slug = normalizeSlug(cinemaName, hallName, baseText);

        // DB kontrolü
        boolean exists = movieRepository.existsBySlug(slug);
        if (exists) {
            throw new IllegalArgumentException("Seçilen salon için aynı slug zaten mevcut: " + slug);
        }

        return slug;
    }

    /**
     * Coming Soon film eklerken title benzersizliğini kontrol eder.
     * Eğer aynı title varsa IllegalArgumentException fırlatır.
     */
    public void validateUniqueComingSoonTitle(String title) {
        boolean exists = movieRepository.existsByTitleAndStatus(title, MovieStatus.COMING_SOON);
        if (exists) {
            throw new IllegalArgumentException("Bu film zaten vizyona girmeyi bekliyor: " + title);
        }
    }

}
