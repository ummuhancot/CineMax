package com.cinemax.controller.user;

import com.cinemax.controller.businnes.MovieController;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.request.business.MovieRequest;
import com.cinemax.payload.response.business.MovieAdminResponse;
import com.cinemax.payload.response.business.MovieResponse;
import com.cinemax.payload.response.business.MovieShowTimesResponse;
import com.cinemax.service.bussines.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieControllerTest {

    @Mock
    private MovieService movieService;

    @InjectMocks
    private MovieController movieController;

    private MovieRequest movieRequest;
    private MovieResponse movieResponse;
    private MovieAdminResponse movieAdminResponse;
    private MovieShowTimesResponse movieShowTimesResponse;
    private Long movieId;

    @BeforeEach
    void setUp() {
        movieId = 1L;

        movieRequest = MovieRequest.builder()
                .title("Test Movie")
                .summary("Test Summary")
                .releaseDate(LocalDate.now().plusDays(10))
                .durationDays(15)
                .director("Test Director")
                .genre("Test Genre")
                .hallIds(List.of(1L))
                .cast(List.of("Actor 1"))
                .formats(List.of("2D"))
                .build();

        movieResponse = MovieResponse.builder()
                .id(movieId)
                .title("Test Movie")
                .slug("test-movie")
                .summary("Test Summary")
                .releaseDate(LocalDate.now().plusDays(10))
                .duration(15)
                .genre("Test Genre")
                .build();

        movieAdminResponse = MovieAdminResponse.builder()
                .id(movieId)
                .title("Test Movie")
                .slug("test-movie")
                .releaseDate(LocalDate.now().plusDays(10))
                .build();

        movieShowTimesResponse = MovieShowTimesResponse.builder()
                .movieId(movieId)
                .title("Test Movie")
                .showTimes(Collections.emptyList())
                .build();
    }

    // --- createMovie ---
    @Test
    @DisplayName("POST /save - Film oluşturma başarılı")
    void createMovie_shouldReturnOk() {
        when(movieService.saveMovie(any(MovieRequest.class))).thenReturn(movieResponse);

        ResponseEntity<MovieResponse> response = movieController.createMovie(movieRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(movieResponse, response.getBody());
        verify(movieService).saveMovie(movieRequest);
    }

    // --- updateMovie ---
    @Test
    @DisplayName("PUT /update/{id} - Film güncelleme başarılı")
    void updateMovie_shouldReturnOk() {
        when(movieService.updateMovie(eq(movieId), any(MovieRequest.class))).thenReturn(movieResponse);

        ResponseEntity<MovieResponse> response = movieController.updateMovie(movieId, movieRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(movieResponse, response.getBody());
        verify(movieService).updateMovie(movieId, movieRequest);
    }

    @Test
    @DisplayName("PUT /update/{id} - Film bulunamadı")
    void updateMovie_notFound_shouldThrowException() {
        when(movieService.updateMovie(eq(movieId), any(MovieRequest.class)))
                .thenThrow(new ResourceNotFoundException("Movie not found"));

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> movieController.updateMovie(movieId, movieRequest));

        assertEquals("Movie not found", ex.getMessage());
    }

    // --- deleteMovie ---
    @Test
    @DisplayName("DELETE /{id} - Film silme başarılı")
    void deleteMovie_shouldReturnOk() {
        when(movieService.deleteById(movieId)).thenReturn(movieResponse);

        ResponseEntity<MovieResponse> response = movieController.deleteMovie(movieId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(movieResponse, response.getBody());
        verify(movieService).deleteById(movieId);
    }

    // --- getShowTimes ---
    @Test
    @DisplayName("GET /{id}/show-times - Seansları getirir")
    void getShowTimes_shouldReturnOk() {
        when(movieService.getUpcomingShowTimes(movieId)).thenReturn(movieShowTimesResponse);

        ResponseEntity<MovieShowTimesResponse> response = movieController.getShowTimes(movieId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(movieShowTimesResponse, response.getBody());
    }

    // --- getMoviesByHallType ---
    @Test
    @DisplayName("GET /hall/{hallType} - Salona göre filmleri getirir")
    void getMoviesByHallType_shouldReturnList() {
        when(movieService.getMoviesByHallType(anyString(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(List.of(movieResponse));

        List<MovieResponse> result = movieController.getMoviesByHall("IMAX", 0, 10, "title", "ASC");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(movieService).getMoviesByHallType("IMAX", 0, 10, "title", "ASC");
    }

    // --- getMoviesInTheaters ---
    @Test
    @DisplayName("GET /in-theaters - Vizyondaki filmleri getirir")
    void getMoviesInTheaters_shouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("releaseDate").descending());
        Page<MovieResponse> page = new PageImpl<>(List.of(movieResponse));

        when(movieService.getMoviesInTheaters(pageable)).thenReturn(page);

        ResponseEntity<Page<MovieResponse>> response = movieController.getMoviesInTheaters(pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getContent().size());
    }

    // --- getActiveMoviesInTheaters ---
    @Test
    @DisplayName("GET /in-theaters/active - Aktif vizyon filmlerini getirir")
    void getActiveMoviesInTheaters_shouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("releaseDate").descending());
        Page<MovieResponse> page = new PageImpl<>(List.of(movieResponse));

        when(movieService.getMoviesInTheatersWithDateCheck(pageable)).thenReturn(page);

        ResponseEntity<Page<MovieResponse>> response = movieController.getActiveMoviesInTheaters(pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
    }

    // --- getComingSoon ---
    @Test
    @DisplayName("GET /coming-soon - Yakında gelecek filmleri getirir")
    void getComingSoon_shouldReturnList() {
        when(movieService.getComingSoon(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(List.of(movieResponse));

        ResponseEntity<List<MovieResponse>> response =
                movieController.getComingSoon(0, 10, "releaseDate", "asc");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    // --- getMovieById ---
    @Test
    @DisplayName("GET /getOneMovie/{id} - Tek filmi getirir")
    void getMovieById_shouldReturnMovie() {
        when(movieService.getMovieById(movieId)).thenReturn(movieResponse);

        ResponseEntity<MovieResponse> response = movieController.getMovieById(movieId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(movieResponse, response.getBody());
    }

    // --- getMovieByIdAdmin ---
    @Test
    @DisplayName("GET /{id}/admin - Admin için tek filmi getirir")
    void getMovieByIdAdmin_shouldReturnAdminMovie() {
        when(movieService.getMovieByIdAdmin(movieId)).thenReturn(movieAdminResponse);

        ResponseEntity<MovieAdminResponse> response = movieController.getMovieByIdAdmin(movieId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(movieAdminResponse, response.getBody());
    }

    // --- getAllMovies ---
    @Test
    @DisplayName("GET /getAllMovies - Sayfalı film listesini getirir")
    void getAllMovies_shouldReturnPagedMovies() {
        Page<MovieResponse> page = new PageImpl<>(List.of(movieResponse));

        when(movieService.getAllMovies(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(page);

        ResponseEntity<Page<MovieResponse>> response =
                movieController.getAllMovies(0, 10, "title", "ASC");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getContent().size());
    }

    // --- saveMovies ---
    @Test
    @DisplayName("POST /bulk - Çoklu film kaydetme başarılı")
    void saveMovies_shouldReturnList() {
        List<MovieRequest> requests = List.of(movieRequest);
        List<MovieResponse> responses = List.of(movieResponse);

        when(movieService.saveMovies(requests)).thenReturn(responses);

        ResponseEntity<List<MovieResponse>> response = movieController.saveMovies(requests);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }
}
