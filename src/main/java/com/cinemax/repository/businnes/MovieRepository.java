package com.cinemax.repository.businnes;

import com.cinemax.entity.concretes.business.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    boolean existsBySlug(String slug);
    Page<Movie> findByHalls_Name(String hallName, Pageable pageable);

}
