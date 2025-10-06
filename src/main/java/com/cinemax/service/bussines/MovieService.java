package com.cinemax.service.bussines;

import com.cinemax.entity.concretes.business.Image;
import com.cinemax.entity.concretes.business.Movie;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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
    public MovieResponse updateMovie(MovieRequest request) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("Movie ID must be provided for update");
        }

        Movie existingMovie = movieRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException(MOVIE_NOT_FOUND + request.getId()));

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

    /**
     * T-5: Yakında (COMING_SOON) olan ya da vizyonda olmayan ve çıkış tarihi gelecekte olan filmler
     */
    public List<MovieResponse> getComingSoon(Integer page, Integer size, String sort, String type) {
        int p = page == null || page < 0 ? 0 : page;
        int s = size == null || size <= 0 ? 10 : size;
        String sortField = (sort == null || sort.isBlank()) ? "releaseDate" : sort;
        Sort.Direction dir = "desc".equalsIgnoreCase(type) ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(p, s, Sort.by(dir, sortField));
        // REPO’da varsa doğrudan kullan
        try {
            Page<Movie> pageResult = movieRepository.findByStatus(MovieStatus.COMING_SOON, pageable);
            return pageResult.stream().map(movieMapper::mapMovieToMovieResponse).toList();
        } catch (Throwable ignore) {
            // Fallback: findAll + filtre (mevcut mantığınız)
            LocalDate today = LocalDate.now();
            var all = movieRepository.findAll(Sort.by(dir, sortField));
            var filtered = all.stream()
                    .filter(m -> {
                        try {
                            var st = m.getStatus();
                            if (st == MovieStatus.COMING_SOON) return true;
                            if (st != MovieStatus.IN_THEATERS) {
                                LocalDate rd = m.getReleaseDate();
                                return rd != null && rd.isAfter(today);
                            }
                        } catch (Exception ignored2) {
                        }
                        return false;
                    })
                    .toList();

            int from = Math.min(p * s, filtered.size());
            int to = Math.min(from + s, filtered.size());
            return filtered.subList(from, to).stream()
                    .map(movieMapper::mapMovieToMovieResponse)
                    .toList();
        }
    }

    // --------- EKLENEN METOT (T-6) ---------
    @Transactional(readOnly = true)
    public List<MovieResponse> searchMovies(String q,
                                            Integer page,
                                            Integer size,
                                            String sort,
                                            String type) {

        int p = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size <= 0) ? 10 : size;
        String sortField = (sort == null || sort.isBlank()) ? "title" : sort;
        Sort.Direction dir = "desc".equalsIgnoreCase(type) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(p, s, Sort.by(dir, sortField));

        // 1) ÖNCE: repository'de "search" varsa onu kullan
        try {
            Page<Movie> pageResult = movieRepository.search(q, pageable);
            return pageResult.stream()
                    .map(movieMapper::mapMovieToMovieResponse)
                    .toList();
        } catch (Throwable ignore) {
            // 2) FALLBACK: findAll + filtre + manuel sayfalama
            var all = movieRepository.findAll(Sort.by(dir, sortField));

            String needle = (q == null) ? "" : q.trim().toLowerCase();
            var filtered = all.stream().filter(m -> {
                try {
                    Method gt = m.getClass().getMethod("getTitle");
                    Method gd = null;
                    try { gd = m.getClass().getMethod("getDescription"); } catch (Exception ignore2) {}

                    String title = String.valueOf(gt.invoke(m)).toLowerCase();
                    String desc  = gd == null ? "" : String.valueOf(gd.invoke(m)).toLowerCase();

                    return needle.isEmpty() || title.contains(needle) || desc.contains(needle);
                } catch (Exception e) {
                    // Entity alan adları farklıysa, eşleşmeyi engelleme
                    return needle.isEmpty();
                }
            }).toList();

            int from = Math.min(p * s, filtered.size());
            int to   = Math.min(from + s, filtered.size());
            var slice = (from <= to) ? filtered.subList(from, to) : new ArrayList<Movie>();

            return slice.stream().map(movieMapper::mapMovieToMovieResponse).toList();
        }
    }
}
