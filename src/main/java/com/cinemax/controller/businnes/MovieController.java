package com.cinemax.controller.businnes;

import com.cinemax.payload.request.business.MovieRequest;
import com.cinemax.payload.response.business.MovieAdminResponse;
import com.cinemax.payload.response.business.MovieResponse;
import com.cinemax.payload.response.business.MovieShowTimesResponse;
import com.cinemax.service.bussines.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<MovieResponse> createMovie(@Valid @RequestBody MovieRequest request) {
        MovieResponse response = movieService.saveMovie(request);
        return ResponseEntity.ok(response);
    }

    //çalışıyor ıd ile gönderiyorum url de id görünmüyor
    @PutMapping("/update")
    public ResponseEntity<MovieResponse> updateMovie(@Valid @RequestBody MovieRequest request) {
        MovieResponse response = movieService.updateMovie(request);
        return ResponseEntity.ok(response);
    }


    //kodun devam yok yaz
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

    //çalışıyor
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

    //kodu düzelt movieIntheaters için kod yaz
    //çalışmıyor kod bak
    @GetMapping("/in-theaters")
    public ResponseEntity<Page<MovieResponse>> getMoviesInTheaters(
            @PageableDefault(size = 10, sort = "releaseDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(movieService.getMoviesInTheaters(pageable));
    }

    //çalışmıyor koda bak
    // 2) Status + releaseDate kontrolü
    @GetMapping("/in-theaters/active")
    public ResponseEntity<Page<MovieResponse>> getActiveMoviesInTheaters(
            @PageableDefault(size = 10, sort = "releaseDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(movieService.getMoviesInTheatersWithDateCheck(pageable));
    }

   //çalışıyor ama mantık hatası
    @GetMapping("/coming-soon")
    public ResponseEntity<List<MovieResponse>> getComingSoon(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "releaseDate") String sort,
            @RequestParam(required = false, defaultValue = "asc") String type) {

        var list = movieService.getComingSoon(page, size, sort, type);
        return ResponseEntity.ok(list);
    }

    //çalışıyor
    @GetMapping("/getOneMovie/{id}")
    @PreAuthorize("hasAnyAuthority('Admin','Manager','Customer')")
    public ResponseEntity<MovieResponse> getMovieById(@PathVariable Long id) {
        MovieResponse movie = movieService.getMovieById(id); // Service metodu ID ile filmi döndürür
        return ResponseEntity.ok(movie);
    }

    //çalışıyor
    //admine özel film özet olarak getiyor
    @GetMapping("/{id}/admin")
    public ResponseEntity<MovieAdminResponse> getMovieByIdAdmin(@PathVariable Long id) {
        MovieAdminResponse movie = movieService.getMovieByIdAdmin(id);
        return ResponseEntity.ok(movie);
    }

    @GetMapping("/getAllMovies")
    public ResponseEntity<List<MovieResponse>> getAllMovies() {
        List<MovieResponse> movies = movieService.getAllMovies();
        return ResponseEntity.ok(movies);
    }

    //çalışıyor
    @PostMapping("/bulk")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public ResponseEntity<List<MovieResponse>> saveMovies(@RequestBody List<MovieRequest> requests) {
        List<MovieResponse> responses = movieService.saveMovies(requests);
        return ResponseEntity.ok(responses);
    }



}
