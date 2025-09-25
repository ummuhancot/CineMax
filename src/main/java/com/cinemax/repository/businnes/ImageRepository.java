package com.cinemax.repository.businnes;

import com.cinemax.entity.concretes.business.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
