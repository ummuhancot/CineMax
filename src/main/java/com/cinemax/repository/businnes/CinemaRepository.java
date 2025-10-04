package com.cinemax.repository.businnes;

import com.cinemax.entity.concretes.business.Cinema;
import com.cinemax.entity.enums.HallType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CinemaRepository extends JpaRepository<Cinema, Long> {

    // Şehir ve/veya özel salon parametreleriyle filtreleme

    @Query("SELECT DISTINCT c FROM Cinema c " +
            "LEFT JOIN c.halls h " +
            "WHERE (:city IS NULL OR LOWER(c.city.name) = LOWER(:city)) " +
            "AND (:specialHall IS NULL OR h.type = :specialHall)")
    List<Cinema> findCinemasByCityAndSpecialHall(@Param("city") String city,
                                                 @Param("specialHall") HallType specialHall);

    // Belirli bir şehre ait tüm sinemaları getir
    List<Cinema> findByCityId(Long cityId);

    // Şehre ait belirli sinema var mı kontrol et
    boolean existsByIdAndCityId(Long cinemaId, Long cityId);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsBySlug(String slug);

    Optional<Cinema> findByEmailIgnoreCase(String email);
    Optional<Cinema> findByPhoneNumber(String phoneNumber);


}
