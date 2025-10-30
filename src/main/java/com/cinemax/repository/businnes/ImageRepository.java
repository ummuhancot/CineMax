package com.cinemax.repository.businnes;

import com.cinemax.entity.concretes.business.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByMovieId(Long movieId);

    boolean existsByData(byte[] data);

}
