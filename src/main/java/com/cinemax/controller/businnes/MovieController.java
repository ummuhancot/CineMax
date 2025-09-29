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

    // T-6: arama + paging
    @GetMapping
    public ResponseEntity<Page<MovieResponse>> getMovies(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "20") Integer size,
            @RequestParam(value = "sort", defaultValue = "title") String sort,
            @RequestParam(value = "type", defaultValue = "asc") String type
    ) {
        return ResponseEntity.ok(movieService.search(q, page, size, sort, type));
    }

    // T-5: coming-soon
    @GetMapping("/coming-soon")
    public ResponseEntity<Page<MovieResponse>> getComingSoon(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "20") Integer size,
            @RequestParam(value = "sort", defaultValue = "releaseDate") String sort,
            @RequestParam(value = "type", defaultValue = "asc") String type
    ) {
        return ResponseEntity.ok(movieService.getComingSoon(page, size, sort, type));
    }

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('Admin')")
    public ResponseEntity<MovieResponse> movieSave(
            @Valid @RequestBody MovieRequest movieRequest){
        MovieResponse response = movieService.save(movieRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('Admin')")
    public ResponseEntity<MovieResponse> updateMovie(
            @PathVariable Long id,
            @RequestBody MovieRequest movieRequest) {
        MovieResponse updatedMovie = movieService.updateMovie(id, movieRequest);
        return ResponseEntity.ok(updatedMovie);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('Admin')")
    public ResponseEntity<MovieResponse> deleteMovie(@PathVariable Long id){
        MovieResponse deletedMovie = movieService.deleteById(id);
        return ResponseEntity.ok(deletedMovie);
    }

    @GetMapping("/{id}/show-times")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager','Customer')")
    public ResponseEntity<MovieShowTimesResponse> getShowTimes(@PathVariable Long id) {
        MovieShowTimesResponse response = movieService.getUpcomingShowTimes(id);
        return ResponseEntity.ok(response);
    }

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

    // 1) Sadece status ile kontrol
    @GetMapping("/in-theaters")
    public ResponseEntity<Page<MovieResponse>> getMoviesInTheaters(
            @PageableDefault(size = 10, sort = "releaseDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(movieService.getMoviesInTheaters(pageable));
    }

    // 2) Status + releaseDate kontrolü
    @GetMapping("/in-theaters/active")
    public ResponseEntity<Page<MovieResponse>> getActiveMoviesInTheaters(
            @PageableDefault(size = 10, sort = "releaseDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(movieService.getMoviesInTheatersWithDateCheck(pageable));
    }

    // /{id} (public detay – sadece sayısal id)
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<MovieResponse> getMovieById(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getMovieById(id));
    }

    // T-8: /{id}/admin (Admin only)
    @GetMapping("/{id:\\d+}/admin")
    @PreAuthorize("hasAnyAuthority('Admin')")
    public ResponseEntity<MovieResponse> getMovieByIdAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getMovieById(id));
    }
}
