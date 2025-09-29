package com.cinemax.repository.businnes;

import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.entity.enums.MovieStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    boolean existsBySlug(String slug);
    Page<Movie> findByHalls_Name(String hallName, Pageable pageable);
    // 1) Sadece status ile (vizyonda olanlar)
    Page<Movie> findByStatus(MovieStatus status, Pageable pageable);

    // 2) Hem status hem tarih ile (vizyonda olup gösterim tarihi başlamış olanlar)
    Page<Movie> findByStatusAndReleaseDateBefore(
            MovieStatus status,
            LocalDate today,
            Pageable pageable
    );
}
