package com.cinemax.repository.businnes;

import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.entity.enums.MovieStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface MovieRepository extends
        JpaRepository<Movie, Long>,
        JpaSpecificationExecutor<Movie> { // Specification + paging/sorting

    boolean existsBySlug(String slug);

    Page<Movie> findByHalls_Name(String hallName, Pageable pageable);

    // Vizyonda olanlar (status)
    Page<Movie> findByStatus(MovieStatus status, Pageable pageable);

    // Vizyonda olup gösterim tarihi başlamış olanlar (status + tarih, "in-theaters/active")
    Page<Movie> findByStatusAndReleaseDateBefore(
            MovieStatus status,
            LocalDate today,
            Pageable pageable
    );

    // T-5: Yakında vizyona girecekler (status + ileri tarih)
    Page<Movie> findByStatusAndReleaseDateAfter(
            MovieStatus status,
            LocalDate after,
            Pageable pageable
    );
}
