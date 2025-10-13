package com.cinemax.service.validator;

import com.cinemax.entity.concretes.business.Hall;
import com.cinemax.payload.request.business.MovieRequest;
import com.cinemax.repository.businnes.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MovieValidator {

    private final MovieRepository movieRepository;

    public void validateSingleCinema(List<Hall> halls) {
        Set<Long> cinemaIds = halls.stream()
                .map(h -> h.getCinema().getId())
                .collect(Collectors.toSet());
        if (cinemaIds.size() != 1) {
            throw new IllegalArgumentException("Tüm salonlar aynı sinemaya ait olmalı");
        }
    }

    // 🔹 Tek hall kontrolü (aynı film birden fazla hall'e kaydedilemez)
    public void validateUniqueMovieInHalls(String title, List<Hall> halls) {
        for (Hall hall : halls) {
            if (movieRepository.existsByTitleAndHalls_Id(title, hall.getId())) {
                throw new IllegalArgumentException(
                        "Bu film zaten '" + hall.getName() + "' salonunda kayıtlı."
                );
            }
        }
    }

    public static String generateUniqueSlug(MovieRequest request, List<Hall> halls, MovieRepository movieRepository) {
        String cinemaName = halls.get(0).getCinema().getName();
        String hallName = halls.get(0).getName();

        String baseSlug = request.getSlug() != null
                ? normalizeSlug(cinemaName, hallName, request.getSlug())
                : normalizeSlug(cinemaName, hallName, request.getTitle());

        String slug = baseSlug;
        int count = 1;
        while (movieRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + count;
            count++;
        }

        return slug;
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



}
