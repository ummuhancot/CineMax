package com.cinemax.repository.businnes;

import com.cinemax.entity.concretes.business.Hall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HallRepository extends JpaRepository<Hall, Long> {

    boolean existsByNameAndCinemaId(String name, Long cinemaId);

    List<Hall> findByCinemaId(Long cinemaId);
}
