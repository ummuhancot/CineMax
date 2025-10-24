package com.cinemax.controller.user;

import com.cinemax.controller.businnes.MovieController;
import com.cinemax.exception.ResourceNotFoundException; // Hata senaryoları için
import com.cinemax.payload.request.business.MovieRequest;
import com.cinemax.payload.response.business.MovieAdminResponse;
import com.cinemax.payload.response.business.MovieResponse;
import com.cinemax.payload.response.business.MovieShowTimesResponse;
import com.cinemax.service.bussines.MovieService; // Test edilecek sınıfın bağımlılığı
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl; // PageImpl import edildi
import org.springframework.data.domain.PageRequest; // PageRequest import edildi
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any; // any() import edildi
import static org.mockito.ArgumentMatchers.anyInt; // anyInt() import edildi
import static org.mockito.ArgumentMatchers.anyLong; // anyLong() import edildi
import static org.mockito.ArgumentMatchers.anyString; // anyString() import edildi
import static org.mockito.ArgumentMatchers.eq; // eq() import edildi
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // JUnit 5 ve Mockito entegrasyonu
class MovieControllerTest {

    @Mock // Sahte (mock) bir MovieService nesnesi oluşturur
    private MovieService movieService;

    @InjectMocks // Mocklanan movieService nesnesini movieController'a enjekte eder
    private MovieController movieController;

    // Testlerde kullanılacak örnek değişkenler
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
                .duration(15) // MovieResponse'da duration
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

    // --- createMovie (POST /save) Testleri ---
    @Test
    @DisplayName("POST /save - Film Oluşturma Başarılı")
    void createMovie_whenValidRequest_shouldReturnOk() {
        // Arrange
        when(movieService.saveMovie(any(MovieRequest.class))).thenReturn(movieResponse);

        // Act
        ResponseEntity<MovieResponse> responseEntity = movieController.createMovie(movieRequest);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode()); // Controller OK dönüyor
        assertNotNull(responseEntity.getBody());
        assertEquals(movieResponse.getTitle(), responseEntity.getBody().getTitle());

        // Verify
        verify(movieService, times(1)).saveMovie(movieRequest);
        verifyNoMoreInteractions(movieService);
    }

    // --- updateMovie (PUT /update/{id}) Testleri ---
    @Test
    @DisplayName("PUT /update/{id} - Film Güncelleme Başarılı")
    void updateMovie_whenExists_shouldReturnOk() {
        // Arrange
        when(movieService.updateMovie(eq(movieId), any(MovieRequest.class))).thenReturn(movieResponse);

        // Act
        ResponseEntity<MovieResponse> responseEntity = movieController.updateMovie(movieId, movieRequest);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(movieResponse, responseEntity.getBody());

        // Verify
        verify(movieService, times(1)).updateMovie(movieId, movieRequest);
        verifyNoMoreInteractions(movieService);
    }

    @Test
    @DisplayName("PUT /update/{id} - Film Bulunamadı")
    void updateMovie_whenNotFound_shouldThrowResourceNotFound() {
        // Arrange
        when(movieService.updateMovie(eq(movieId), any(MovieRequest.class)))
                .thenThrow(new ResourceNotFoundException("Movie not found"));

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            movieController.updateMovie(movieId, movieRequest);
        });
        assertEquals("Movie not found", exception.getMessage());

        // Verify
        verify(movieService, times(1)).updateMovie(movieId, movieRequest);
        verifyNoMoreInteractions(movieService);
    }

    // --- deleteMovie (DELETE /{id}) Testleri ---
    @Test
    @DisplayName("DELETE /{id} - Film Silme Başarılı")
    void deleteMovie_whenExists_shouldReturnOk() {
        // Arrange
        when(movieService.deleteById(movieId)).thenReturn(movieResponse);

        // Act
        ResponseEntity<MovieResponse> responseEntity = movieController.deleteMovie(movieId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(movieResponse, responseEntity.getBody());

        // Verify
        verify(movieService, times(1)).deleteById(movieId);
        verifyNoMoreInteractions(movieService);
    }

    // --- getShowTimes (GET /{id}/show-times) Testleri ---
    @Test
    @DisplayName("GET /{id}/show-times - Seansları Getirme Başarılı")
    void getShowTimes_whenExists_shouldReturnOk() {
        // Arrange
        when(movieService.getUpcomingShowTimes(movieId)).thenReturn(movieShowTimesResponse);

        // Act
        ResponseEntity<MovieShowTimesResponse> responseEntity = movieController.getShowTimes(movieId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(movieShowTimesResponse, responseEntity.getBody());

        // Verify
        verify(movieService, times(1)).getUpcomingShowTimes(movieId);
        verifyNoMoreInteractions(movieService);
    }

    // --- getMoviesByHall (GET /{hall}) Testleri ---
    @Test
    @DisplayName("GET /{hall} - Salona Göre Film Getirme Başarılı")
    void getMoviesByHall_shouldReturnList() {
        // Arrange
        String hallName = "IMAX";
        int page = 0;
        int size = 5;
        String sort = "title";
        String type = "ASC";
        List<MovieResponse> responseList = List.of(movieResponse);
        when(movieService.getMoviesByHall(hallName, page, size, sort, type)).thenReturn(responseList);

        // Act
        List<MovieResponse> actualList = movieController.getMoviesByHall(hallName, page, size, sort, type);

        // Assert
        assertNotNull(actualList);
        assertEquals(1, actualList.size());
        assertEquals(movieResponse, actualList.get(0));

        // Verify
        verify(movieService, times(1)).getMoviesByHall(hallName, page, size, sort, type);
        verifyNoMoreInteractions(movieService);
    }

    // --- getMoviesInTheaters (GET /in-theaters) Testleri ---
    @Test
    @DisplayName("GET /in-theaters - Vizyondaki Filmleri Getirme Başarılı")
    void getMoviesInTheaters_shouldReturnPage() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "releaseDate"));
        Page<MovieResponse> responsePage = new PageImpl<>(List.of(movieResponse), pageable, 1);
        when(movieService.getMoviesInTheaters(pageable)).thenReturn(responsePage);

        // Act
        ResponseEntity<Page<MovieResponse>> responseEntity = movieController.getMoviesInTheaters(pageable);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(1, responseEntity.getBody().getTotalElements());
        assertEquals(movieResponse, responseEntity.getBody().getContent().get(0));

        // Verify
        verify(movieService, times(1)).getMoviesInTheaters(pageable);
        verifyNoMoreInteractions(movieService);
    }

    // --- getActiveMoviesInTheaters (GET /in-theaters/active) Testleri ---
    @Test
    @DisplayName("GET /in-theaters/active - Aktif Vizyon Filmlerini Getirme Başarılı")
    void getActiveMoviesInTheaters_shouldReturnPage() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "releaseDate"));
        Page<MovieResponse> responsePage = new PageImpl<>(List.of(movieResponse), pageable, 1);
        when(movieService.getMoviesInTheatersWithDateCheck(pageable)).thenReturn(responsePage);

        // Act
        ResponseEntity<Page<MovieResponse>> responseEntity = movieController.getActiveMoviesInTheaters(pageable);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(1, responseEntity.getBody().getTotalElements());

        // Verify
        verify(movieService, times(1)).getMoviesInTheatersWithDateCheck(pageable);
        verifyNoMoreInteractions(movieService);
    }

    // --- getComingSoon (GET /coming-soon) Testleri ---
    @Test
    @DisplayName("GET /coming-soon - Yakında Gelecek Filmleri Getirme Başarılı")
    void getComingSoon_shouldReturnList() {
        // Arrange
        Integer page = 0;
        Integer size = 10;
        String sort = "releaseDate";
        String type = "asc";
        List<MovieResponse> responseList = List.of(movieResponse);
        when(movieService.getComingSoon(page, size, sort, type)).thenReturn(responseList);

        // Act
        ResponseEntity<List<MovieResponse>> responseEntity = movieController.getComingSoon(page, size, sort, type);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(1, responseEntity.getBody().size());

        // Verify
        verify(movieService, times(1)).getComingSoon(page, size, sort, type);
        verifyNoMoreInteractions(movieService);
    }

    // --- getMovieById (GET /getOneMovie/{id}) Testleri ---
    @Test
    @DisplayName("GET /getOneMovie/{id} - Tek Film Getirme Başarılı")
    void getMovieById_whenExists_shouldReturnOk() {
        // Arrange
        when(movieService.getMovieById(movieId)).thenReturn(movieResponse);

        // Act
        ResponseEntity<MovieResponse> responseEntity = movieController.getMovieById(movieId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(movieResponse, responseEntity.getBody());

        // Verify
        verify(movieService, times(1)).getMovieById(movieId);
        verifyNoMoreInteractions(movieService);
    }

    // --- getMovieByIdAdmin (GET /{id}/admin) Testleri ---
    @Test
    @DisplayName("GET /{id}/admin - Admin İçin Tek Film Getirme Başarılı")
    void getMovieByIdAdmin_whenExists_shouldReturnOk() {
        // Arrange
        when(movieService.getMovieByIdAdmin(movieId)).thenReturn(movieAdminResponse);

        // Act
        ResponseEntity<MovieAdminResponse> responseEntity = movieController.getMovieByIdAdmin(movieId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(movieAdminResponse, responseEntity.getBody());

        // Verify
        verify(movieService, times(1)).getMovieByIdAdmin(movieId);
        verifyNoMoreInteractions(movieService);
    }

    // --- getAllMovies (GET /getAllMovies) Testleri ---
    @Test
    @DisplayName("GET /getAllMovies - Tüm Filmleri Getirme Başarılı")
    void getAllMovies_shouldReturnList() {
        // Arrange
        List<MovieResponse> responseList = List.of(movieResponse);
        when(movieService.getAllMovies()).thenReturn(responseList);

        // Act
        ResponseEntity<List<MovieResponse>> responseEntity = movieController.getAllMovies();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(1, responseEntity.getBody().size());

        // Verify
        verify(movieService, times(1)).getAllMovies();
        verifyNoMoreInteractions(movieService);
    }

    // --- saveMovies (POST /bulk) Testleri ---
    @Test
    @DisplayName("POST /bulk - Çoklu Film Kaydetme Başarılı")
    void saveMovies_whenValidRequests_shouldReturnOk() {
        // Arrange
        List<MovieRequest> requestList = List.of(movieRequest);
        List<MovieResponse> responseList = List.of(movieResponse);
        when(movieService.saveMovies(requestList)).thenReturn(responseList);

        // Act
        ResponseEntity<List<MovieResponse>> responseEntity = movieController.saveMovies(requestList);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(1, responseEntity.getBody().size());

        // Verify
        verify(movieService, times(1)).saveMovies(requestList);
        verifyNoMoreInteractions(movieService);
    }
}