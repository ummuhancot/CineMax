package com.cinemax.controller.businnes;

import com.cinemax.payload.request.business.MovieRequest;
import com.cinemax.payload.response.business.MovieResponse;
import com.cinemax.payload.response.business.MovieShowTimesResponse;
import com.cinemax.service.bussines.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    //Çalışıyor
    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('Admin')")
    public ResponseEntity<MovieResponse> movieSave(@Valid @RequestBody MovieRequest movieRequest) {
        MovieResponse response = movieService.save(movieRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //KOD çalışıyor ıd ile getirmeyi cıkardım
    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('Admin')")
    public ResponseEntity<MovieResponse> updateMovie(
            @RequestBody MovieRequest movieRequest) {

        // movieRequest içinde id olmalı
        MovieResponse updatedMovie = movieService.updateMovie(movieRequest);
        return ResponseEntity.ok(updatedMovie);
    }

    // >>> EKLENEN ENDPOINT: /api/movies/{id} (PathVariable ile)
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('Admin')")
    public ResponseEntity<MovieResponse> updateMovieById(@PathVariable("id") Long movieId,
                                                         @Valid @RequestBody MovieRequest request) {
        // path'ten gelen id'yi request'e taşı
        request.setId(movieId);
        MovieResponse dto = movieService.updateMovie(request); // servisin tek parametreli metodu
        return ResponseEntity.ok(dto);
    }

    //çalışıyor
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('Admin')")
    public ResponseEntity<MovieResponse> deleteMovie(@PathVariable Long id) {
        MovieResponse deletedMovie = movieService.deleteById(id);
        return ResponseEntity.ok(deletedMovie);
    }

    //çalışıyor
    @GetMapping("/{id}/show-times")
    @PreAuthorize("hasAnyAuthority('Admin','Manager','Customer')")
    public ResponseEntity<MovieShowTimesResponse> getShowTimes(@PathVariable Long id) {
        MovieShowTimesResponse response = movieService.getUpcomingShowTimes(id);
        return ResponseEntity.ok(response);
    }

    //hala movie eklenmedi
    @GetMapping("/{hall}")
    public List<MovieResponse> getMoviesByHall(
            @PathVariable String hall,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sort,
            @RequestParam(defaultValue = "ASC") String type
    ) {
        return movieService.getMoviesByHall(hall, page, size, sort, type);
    }

    //hata yok response dönüyor ama uyarı var
    // 1) Sadece status ile kontrol
    @GetMapping("/in-theaters")
    public ResponseEntity<Page<MovieResponse>> getMoviesInTheaters(
            @PageableDefault(size = 10, sort = "releaseDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(movieService.getMoviesInTheaters(pageable));
    }

    //Çalışıyor
    // 2) Status + releaseDate kontrolü
    @GetMapping("/in-theaters/active")
    public ResponseEntity<Page<MovieResponse>> getActiveMoviesInTheaters(
            @PageableDefault(size = 10, sort = "releaseDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(movieService.getMoviesInTheatersWithDateCheck(pageable));
    }

    // ... diğer endpointler ...

    @GetMapping("/coming-soon")
    public ResponseEntity<List<MovieResponse>> getComingSoon(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "releaseDate") String sort,
            @RequestParam(required = false, defaultValue = "asc") String type) {

        var list = movieService.getComingSoon(page, size, sort, type);
        return ResponseEntity.ok(list);
    }
}
