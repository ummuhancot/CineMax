package com.cinemax.repository.businnes;

import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.entity.enums.MovieStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

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
    // T-6: q = title veya summary içinde (case-insensitive)
    @Query("""
           SELECT m FROM Movie m
           WHERE (:q IS NULL OR :q = '' 
              OR LOWER(m.title)   LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(m.summary) LIKE LOWER(CONCAT('%', :q, '%')))
           """)
    Page<Movie> searchByTitleOrSummary(@Param("q") String q, Pageable pageable);

    // T-5: coming-soon için de kullanacağız (aşağıda ayrıntı)
    Page<Movie> findByStatusInAndReleaseDateAfter(List<MovieStatus> statuses, LocalDate date, Pageable pageable);
}

