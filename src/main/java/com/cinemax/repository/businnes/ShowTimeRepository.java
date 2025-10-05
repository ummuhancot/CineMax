package com.cinemax.repository.businnes;

import com.cinemax.entity.concretes.business.ShowTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ShowTimeRepository extends JpaRepository<ShowTime, Long> {

    @Query("SELECT s FROM ShowTime s " +
            "WHERE s.movie.id = :movieId " +
            "AND (s.date > :today OR (s.date = :today AND s.startTime > :now)) " +
            "ORDER BY s.date, s.startTime")
    List<ShowTime> findUpcomingShowTimes(@Param("movieId") Long movieId,
                                         @Param("today") LocalDate today,
                                         @Param("now") LocalTime now);


    boolean existsByHall_IdAndDateAndStartTime(Long hallId, java.time.LocalDate date, java.time.LocalTime startTime);

}