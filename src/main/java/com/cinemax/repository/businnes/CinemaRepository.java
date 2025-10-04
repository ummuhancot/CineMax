package com.cinemax.repository.businnes;

import com.cinemax.entity.concretes.business.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CinemaRepository extends JpaRepository<Cinema, Long> {

    // Şehir ve/veya özel salon parametreleriyle filtreleme
    @Query("SELECT c FROM Cinema c " +
            "WHERE (:city IS NULL OR LOWER(c.city.name) = LOWER(:city)) " +
            "AND (:specialHall IS NULL OR EXISTS (SELECT h FROM Hall h WHERE h.cinema = c AND LOWER(h.type) = LOWER(:specialHall)))")
    Optional<List<Cinema>> findCinemasByCityAndSpecialHall(String city, String specialHall);

    // Belirli bir şehre ait tüm sinemaları getir
    List<Cinema> findByCityId(Long cityId);

    // Şehre ait belirli sinema var mı kontrol et
    boolean existsByIdAndCityId(Long cinemaId, Long cityId);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsBySlug(String slug);


}
