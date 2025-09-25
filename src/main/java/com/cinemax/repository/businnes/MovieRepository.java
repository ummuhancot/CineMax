package com.cinemax.repository.businnes;

import com.cinemax.entity.concretes.business.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    boolean existsBySlug(String slug);

}
