package com.cinemax.service.bussines;

import com.cinemax.entity.concretes.business.Image;
import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.entity.concretes.business.ShowTime;
import com.cinemax.entity.enums.MovieStatus;
import com.cinemax.exception.ConflictException;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.mappers.MovieMapper;
import com.cinemax.payload.mappers.MovieShowTimesMapper;
import com.cinemax.payload.mappers.ShowTimeMapper;
import com.cinemax.payload.messages.ErrorMessages;
import com.cinemax.payload.request.business.MovieRequest;
import com.cinemax.payload.response.business.MovieResponse;
import com.cinemax.payload.response.business.MovieShowTimesResponse;
import com.cinemax.repository.businnes.HallRepository;
import com.cinemax.repository.businnes.ImageRepository;
import com.cinemax.repository.businnes.MovieRepository;
import com.cinemax.repository.businnes.ShowTimeRepository;
import com.cinemax.repository.spec.MovieSpecifications;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.cinemax.payload.messages.ErrorMessages.*;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final HallRepository hallRepository;
    private final ImageRepository imageRepository;
    private final ShowTimeRepository showTimeRepository;
    private final MovieMapper movieMapper;
    private final ShowTimeMapper showTimeMapper;
    private final MovieShowTimesMapper movieShowTimesMapper;

    /* T-6: /api/movies?q=&page=&size=&sort=&type= */
    public Page<MovieResponse> search(String q,
                                      Integer page,
                                      Integer size,
                                      String sort,
                                      String type) {
        Sort.Direction direction = "DESC".equalsIgnoreCase(type) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));
        Specification<Movie> spec = MovieSpecifications.nameOrDescriptionContains(q);
        return movieRepository.findAll(spec, pageable)
                .map(movieMapper::mapMovieToMovieResponse);
    }

    /* T-5: /api/movies/coming-soon */
    public Page<MovieResponse> getComingSoon(Integer page,
                                             Integer size,
                                             String sort,
                                             String type) {
        Sort.Direction direction = "DESC".equalsIgnoreCase(type) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));
        LocalDate today = LocalDate.now();
        return movieRepository.findByStatusAndReleaseDateAfter(MovieStatus.COMING_SOON, today, pageable)
                .map(movieMapper::mapMovieToMovieResponse);
    }

    /* /api/movies/{id} ve /{id}/admin */
    public MovieResponse getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MOVIE_NOT_FOUND + id));
        return movieMapper.mapMovieToMovieResponse(movie);
    }

    // --- Var olan CRUD ve diğer metotlar (değiştirilmedi) ---
    @Transactional
    public MovieResponse save(@Valid MovieRequest request) {
        String slug = request.getSlug();
        if (slug == null || slug.isBlank()) slug = MovieMapper.generateSlug(request.getTitle());
        if (movieRepository.existsBySlug(slug)) {
            throw new ResourceNotFoundException(ErrorMessages.MOVIE_CREATE_FAILED + " Slug already exists.");
        }
        Image poster = imageRepository.findById(request.getPosterId())
                .orElseThrow(() -> new RuntimeException(ErrorMessages.MOVIE_CREATE_FAILED + " Poster not found."));
        Movie movie = movieMapper.mapMovieRequestToMovie(request);
        movie.setSlug(slug);
        movie.setPoster(poster);
        Movie savedMovie = movieRepository.save(movie);
        return movieMapper.mapMovieToMovieResponse(savedMovie);
    }

    @Transactional
    public MovieResponse updateMovie(Long id, MovieRequest request) {
        Movie existingMovie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MOVIE_NOT_FOUND + id));

        String slug = request.getSlug();
        if (slug == null || slug.isBlank()) slug = MovieMapper.generateSlug(request.getTitle());
        if (!slug.equals(existingMovie.getSlug()) && movieRepository.existsBySlug(slug)) {
            throw new ConflictException(SLUG_ALREADY_EXISTS);
        }
        Image poster = imageRepository.findById(request.getPosterId())
                .orElseThrow(() -> new ResourceNotFoundException(POSTER_NOT_FOUND));

        movieMapper.updateMovieFromRequest(existingMovie, request);
        existingMovie.setSlug(slug);
        existingMovie.setPoster(poster);

        Movie updatedMovie = movieRepository.save(existingMovie);
        return movieMapper.mapMovieToMovieResponse(updatedMovie);
    }

    public MovieResponse deleteById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.MOVIE_DELETE_FAILED + id));
        return movieMapper.mapMovieToMovieResponse(movie);
    }

    public MovieShowTimesResponse getUpcomingShowTimes(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie with id " + movieId + " not found"));
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        var showTimes = showTimeRepository.findUpcomingShowTimes(movieId, today, now);
        return movieShowTimesMapper.mapMovieWithShowTimesToResponse(movie, showTimes);
    }

    public List<MovieResponse> getMoviesByHall(String hall, int page, int size, String sort, String type) {
        Sort.Direction direction = type.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));
        return movieRepository.findByHalls_Name(hall, pageable)
                .stream()
                .map(movieMapper::mapMovieToMovieResponse)
                .toList();
    }

    public Page<MovieResponse> getMoviesInTheaters(Pageable pageable) {
        return movieRepository.findByStatus(MovieStatus.IN_THEATERS, pageable)
                .map(movieMapper::mapMovieToMovieResponse);
    }

    public Page<MovieResponse> getMoviesInTheatersWithDateCheck(Pageable pageable) {
        LocalDate today = LocalDate.now();
        return movieRepository.findByStatusAndReleaseDateBefore(MovieStatus.IN_THEATERS, today, pageable)
                .map(movieMapper::mapMovieToMovieResponse);
    }
}
