package com.cinemax.service.helper;

import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.exception.ImageException;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.mappers.ImageMapper;
import com.cinemax.payload.messages.ErrorMessages;
import com.cinemax.repository.businnes.ImageRepository;
import com.cinemax.repository.businnes.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ImageHelper {

    private final MovieRepository movieRepository;
    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;

    public void validateMovieExists(Long movieId) {
        if (!movieRepository.existsById(movieId)) {
            throw new ResourceNotFoundException(
                    String.format(ErrorMessages.MOVIE_NOT_FOUND, movieId)
            );
        }
    }

    // Aynı film veya farklı filmlerde duplicate kontrolü
    public void validateDuplicateImage(Movie movie, MultipartFile file) {
        // Aynı film içinde aynı isim
        if (movie.getImages() != null && movie.getImages().stream()
                .anyMatch(img -> img.getName().equals(file.getOriginalFilename()))) {
            throw new ImageException("Bu film için aynı isimde bir resim zaten mevcut");
        }

        // Farklı filmlerde aynı içerik
        try {
            byte[] fileBytes = imageMapper.encodeImage(file.getBytes()).getBytes();
            boolean exists = imageRepository.existsByData(fileBytes);
            if (exists) {
                throw new ImageException("Başka bir film için aynı resim zaten mevcut");
            }
        } catch (IOException e) {
            throw new ImageException("Resim verisi okunamadı");
        }
    }


}
