package com.cinemax.service.bussines;

import com.cinemax.entity.concretes.business.Hall;
import com.cinemax.entity.concretes.business.Image;
import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.mappers.MovieMapper;
import com.cinemax.payload.messages.ErrorMessages;
import com.cinemax.payload.messages.SuccessMessages;
import com.cinemax.payload.request.business.MovieRequest;
import com.cinemax.payload.response.business.MovieResponse;
import com.cinemax.payload.response.business.ResponseMessage;
import com.cinemax.repository.businnes.HallRepository;
import com.cinemax.repository.businnes.ImageRepository;
import com.cinemax.repository.businnes.MovieRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final HallRepository hallRepository;
    private final ImageRepository imageRepository;
    private final MovieMapper movieMapper;


    @Transactional
    public MovieResponse save(MovieRequest request) {
        // 1️⃣ Slug oluştur
        String slug = request.getSlug();
        if (slug == null || slug.isBlank()) {
            slug = MovieMapper.generateSlug(request.getTitle());
        }

        if (movieRepository.existsBySlug(slug)) {
            throw new ResourceNotFoundException(ErrorMessages.MOVIE_CREATE_FAILED + " Slug already exists.");
        }

        // 2️⃣ Hall entitylerini bul
        List<Hall> halls = hallRepository.findAllById(request.getHallIds());
        if (halls.size() != request.getHallIds().size()) {
            throw new ResourceNotFoundException(ErrorMessages.MOVIE_CREATE_FAILED + " One or more halls not found.");
        }

        // 3️⃣ Poster entityyi bul
        Image poster = imageRepository.findById(request.getPosterId())
                .orElseThrow(() -> new RuntimeException(ErrorMessages.MOVIE_CREATE_FAILED + " Poster not found."));

        // 4️⃣ DTO → Entity mapleme
        Movie movie = movieMapper.mapMovieRequestToMovie(request);
        movie.setSlug(slug);
        movie.setHalls(halls);
        movie.setPoster(poster);

        // 5️⃣ Kaydet
        Movie savedMovie = movieRepository.save(movie);

        // 6️⃣ Response DTO
        return movieMapper.mapMovieToMovieResponse(savedMovie);
    }





}
