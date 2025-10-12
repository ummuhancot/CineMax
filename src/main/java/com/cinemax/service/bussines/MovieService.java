package com.cinemax.service.bussines;

import com.cinemax.entity.concretes.business.Hall;
import com.cinemax.entity.concretes.business.Image;
import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.entity.concretes.business.ShowTime;
import com.cinemax.entity.enums.MovieStatus;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.mappers.MovieAdminMapper;
import com.cinemax.payload.mappers.MovieMapper;
import com.cinemax.payload.mappers.MovieShowTimesMapper;
import com.cinemax.payload.mappers.ShowTimeMapper;
import com.cinemax.payload.messages.ErrorMessages;
import com.cinemax.payload.request.business.MovieRequest;
import com.cinemax.payload.request.business.ShowTimeRequest;
import com.cinemax.payload.response.business.MovieAdminResponse;
import com.cinemax.payload.response.business.MovieResponse;
import com.cinemax.payload.response.business.MovieShowTimesResponse;
import com.cinemax.repository.businnes.HallRepository;
import com.cinemax.repository.businnes.ImageRepository;
import com.cinemax.repository.businnes.MovieRepository;
import com.cinemax.repository.businnes.ShowTimeRepository;
import com.cinemax.service.helper.MovieHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final ImageRepository imageRepository;
    private final ShowTimeRepository showTimeRepository;
    private final MovieMapper movieMapper;
    private final MovieShowTimesMapper movieShowTimesMapper;
    private final MovieHelper movieHelper;
    private final HallRepository hallRepository;
    private final ShowTimeMapper showTimeMapper;
    private final MovieAdminMapper movieAdminMapper;

    @Transactional
    public MovieResponse saveMovie(MovieRequest request) {
        // Hall listesi
        List<Hall> halls = movieHelper.getHallsOrThrow(request.getHallIds());

        // Poster opsiyonel
        Image poster = null;
        if (request.getPosterId() != null) {
            poster = movieHelper.getPosterOrThrow(request.getPosterId());
        }

        // Movie oluşturma
        Movie movie = movieMapper.mapMovieRequestToMovie(request, halls, poster);

        // Movie kaydet
        movieRepository.save(movie);

        // ShowTime ekleme
        if (request.getShowTimes() != null && !request.getShowTimes().isEmpty()) {
            for (ShowTimeRequest showTimeRequest : request.getShowTimes()) {
                Hall hall = halls.stream()
                        .filter(h -> h.getId().equals(showTimeRequest.getHallId()))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.HALL_NOT_FOUND_FOR_SHOWTIME));

                ShowTime showTime = showTimeMapper.toEntity(showTimeRequest, movie, hall);
                showTimeRepository.save(showTime);
            }
        }

        return movieMapper.mapMovieToMovieResponse(movie);
    }

    @Transactional
    public MovieResponse updateMovieById(Long movieId, MovieRequest request) {
        Movie movie = movieHelper.getMovieOrThrow(movieId);

        List<Hall> halls = movieHelper.getHallsOrThrow(request.getHallIds());

        // Poster opsiyonel
        Image poster = null;
        if (request.getPosterId() != null) {
            poster = movieHelper.getPosterOrThrow(request.getPosterId());
        }

        // Güncelle
        movieMapper.updateMovieFromRequest(movie, request, halls, poster);

        movieRepository.save(movie);

        return movieMapper.mapMovieToMovieResponse(movie);
    }

    @Transactional
    public MovieResponse deleteById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.MOVIE_DELETE_FAILED + id));
        movieRepository.delete(movie);
        return movieMapper.mapMovieToMovieResponse(movie);
    }

    @Transactional(readOnly = true)
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

    public List<MovieResponse> getComingSoon(Integer page, Integer size, String sort, String type) {
        int p = page == null || page < 0 ? 0 : page;
        int s = size == null || size <= 0 ? 10 : size;
        String sortField = (sort == null || sort.isBlank()) ? "releaseDate" : sort;
        Sort.Direction dir = "desc".equalsIgnoreCase(type) ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(p, s, Sort.by(dir, sortField));

        Page<Movie> pageResult = movieRepository.findByStatus(MovieStatus.COMING_SOON, pageable);
        return pageResult.stream()
                .map(movieMapper::mapMovieToMovieResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MovieResponse getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> movieHelper.movieNotFound(id));
        return movieMapper.mapMovieToMovieResponse(movie);
    }

    @Transactional(readOnly = true)
    public MovieAdminResponse getMovieByIdAdmin(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> movieHelper.movieNotFound(id));
        return movieAdminMapper.toAdminResponse(movie);
    }

    @Transactional
    public List<MovieResponse> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(movieMapper::mapMovieToMovieResponse)
                .toList();
    }

}
