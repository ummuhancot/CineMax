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

    // T-6: Arama + sayfalama (entity’de description yoksa sadece title’a göre arama)
    @Query("""
           SELECT m FROM Movie m
           WHERE (:q IS NULL OR TRIM(:q) = '' 
                  OR LOWER(m.title) LIKE LOWER(CONCAT('%', :q, '%')))
           """)
    Page<Movie> search(@Param("q") String q, Pageable pageable);

    boolean existsByTitleAndHalls_Id(String title, Long id);
}


