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


    @Query("""
        SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END 
        FROM ShowTime s 
        WHERE s.hall.id = :hallId 
          AND s.date = :date 
          AND (
                (s.startTime <= :endTime AND s.endTime >= :startTime)
              )
    """)
    boolean existsByHallIdAndDateAndTimeOverlap(@Param("hallId") Long hallId,
                                                @Param("date") LocalDate date,
                                                @Param("startTime") LocalTime startTime,
                                                @Param("endTime") LocalTime endTime);

}