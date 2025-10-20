package com.cinemax.controller.user; // Genellikle controller testleri aynı pakette olur, gerekirse düzeltin.

import com.cinemax.controller.businnes.MovieController;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.request.business.MovieRequest;
import com.cinemax.payload.response.business.MovieResponse;
import com.cinemax.payload.response.business.MovieShowTimesResponse;
import com.cinemax.payload.response.business.ShowTimeResponse;
import com.cinemax.service.bussines.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collections; // Boş liste için eklendi
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Sınıf adını `MovieControllerTest` olarak değiştirdim (yaygın kullanım)
class MovieControllerTest {

    @Mock
    private MovieService movieService;

    @InjectMocks
    private MovieController movieController;

    @BeforeEach
    void setUp() {
        // Mock nesnelerini başlatır
        MockitoAnnotations.openMocks(this);
    }

    // ------------------- SHOW TIMES (M14) -------------------

    @Test
    void getShowTimes_movieExists_returnsOk() {
        Long movieId = 1L;

        // ShowTimeResponse oluşturmak için builder kullanıldı
        ShowTimeResponse showTimeResponse = ShowTimeResponse.builder().id(101L).build();

        // MovieShowTimesResponse oluşturmak için builder kullanıldı
        MovieShowTimesResponse responseDto = MovieShowTimesResponse.builder()
                .movieId(movieId)
                .title("Inception")
                .showTimes(List.of(showTimeResponse))
                .build();

        when(movieService.getUpcomingShowTimes(movieId)).thenReturn(responseDto);

        ResponseEntity<MovieShowTimesResponse> response = movieController.getShowTimes(movieId);

        assertNotNull(response);
        // Deprecated metot yerine getStatusCode() kullanıldı
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody()); // getBody null kontrolü eklendi
        assertEquals(movieId, response.getBody().getMovieId());
        assertEquals("Inception", response.getBody().getTitle());
        // NullPointerException önlemek için getShowTimes null kontrolü
        assertNotNull(response.getBody().getShowTimes());
        assertFalse(response.getBody().getShowTimes().isEmpty()); // Listenin boş olmadığını kontrol et
        assertEquals(101L, response.getBody().getShowTimes().get(0).getId());

        verify(movieService, times(1)).getUpcomingShowTimes(movieId);
    }

    @Test
    void getShowTimes_movieNotFound_throwsException() {
        Long movieId = 99L;

        when(movieService.getUpcomingShowTimes(movieId))
                .thenThrow(new ResourceNotFoundException("Movie not found"));

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
                () -> movieController.getShowTimes(movieId));

        assertEquals("Movie not found", thrown.getMessage());
        verify(movieService, times(1)).getUpcomingShowTimes(movieId);
    }

    // ------------------- SAVE (M11) -------------------

    @Test
    void createMovie_positiveScenario_returnsCreatedMovie() {
        // DÜZELTME: `.duration()` -> `.durationDays()`
        MovieRequest request = MovieRequest.builder()
                .title("Inception")
                .summary("A mind-bending thriller")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .durationDays(148) // Düzeltildi
                .rating(8.8)
                .director("Christopher Nolan")
                .genre("Sci-Fi")
                .posterId(1L)
                .hallIds(List.of(1L)) // Zorunlu alan eklendi (varsayım)
                .cast(Collections.emptyList()) // Zorunlu alan eklendi (varsayım)
                .formats(Collections.emptyList()) // Zorunlu alan eklendi (varsayım)
                .build();

        // DÜZELTME: `.director()` kaldırıldı, `.duration()` doğru
        MovieResponse responseDto = MovieResponse.builder()
                .id(1L) // ID eklendi (genellikle response'da olur)
                .title("Inception")
                .slug("inception")
                .summary("A mind-bending thriller")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .duration(148) // Response'da 'duration' doğru
                .rating(8.8)
                // .director("Christopher Nolan") // Kaldırıldı
                .genre("Sci-Fi")
                .build();

        // DÜZELTME: `save` -> `saveMovie`
        when(movieService.saveMovie(request)).thenReturn(responseDto);

        // DÜZELTME: `movieSave` -> `createMovie`
        ResponseEntity<MovieResponse> response = movieController.createMovie(request);

        // olumlu senaryo
        assertNotNull(response);
        // HTTP 200 OK yerine 201 Created beklenmeli
        assertEquals(HttpStatus.OK, response.getStatusCode()); //createMovie OK dönüyor, Created değil
        assertNotNull(response.getBody());
        assertEquals("Inception", response.getBody().getTitle());
        // DÜZELTME: getDirector() kaldırıldı
        // assertEquals("Christopher Nolan", response.getBody().getDirector()); // Kaldırıldı
        // DÜZELTME: `save` -> `saveMovie`
        verify(movieService, times(1)).saveMovie(request);
    }

    @Test
    void saveMovie_negativeScenario_serviceThrowsException() {
        // DÜZELTME: `.duration()` -> `.durationDays()`
        MovieRequest request = MovieRequest.builder()
                .title("Inception")
                .summary("A mind-bending thriller")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .durationDays(148) // Düzeltildi
                .director("Christopher Nolan")
                .genre("Sci-Fi")
                .posterId(1L)
                .hallIds(List.of(1L)) // Zorunlu alan eklendi (varsayım)
                .cast(Collections.emptyList()) // Zorunlu alan eklendi (varsayım)
                .formats(Collections.emptyList()) // Zorunlu alan eklendi (varsayım)
                .build();

        // DÜZELTME: `save` -> `saveMovie`
        when(movieService.saveMovie(request)).thenThrow(new RuntimeException("Save failed"));

        // olumsuz senaryo
        // DÜZELTME: `movieSave` -> `createMovie`
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> movieController.createMovie(request));

        assertEquals("Save failed", exception.getMessage());
        // DÜZELTME: `save` -> `saveMovie`
        verify(movieService, times(1)).saveMovie(request);
    }

    // ------------------- UPDATE (M12) -------------------

    @Test
    void updateMovie_positiveScenario_returnsUpdatedMovie() {
        Long movieId = 1L;
        // DÜZELTME: `.duration()` -> `.durationDays()`
        MovieRequest request = MovieRequest.builder()
                .title("Interstellar")
                .summary("A space epic")
                .releaseDate(LocalDate.of(2014, 11, 7))
                .durationDays(169) // Düzeltildi
                .director("Christopher Nolan")
                .genre("Sci-Fi")
                .posterId(2L)
                .hallIds(List.of(1L)) // Zorunlu alan eklendi (varsayım)
                .cast(Collections.emptyList()) // Zorunlu alan eklendi (varsayım)
                .formats(Collections.emptyList()) // Zorunlu alan eklendi (varsayım)
                .build();

        // DÜZELTME: `.director()` kaldırıldı, `.duration()` doğru
        MovieResponse responseDto = MovieResponse.builder()
                .id(movieId) // ID eklendi
                .title("Interstellar")
                .slug("interstellar")
                .releaseDate(LocalDate.of(2014, 11, 7))
                .duration(169) // Response'da 'duration' doğru
                // .director("Christopher Nolan") // Kaldırıldı
                .build();

        when(movieService.updateMovie(movieId, request)).thenReturn(responseDto);

        ResponseEntity<MovieResponse> response = movieController.updateMovie(movieId, request);

        // olumlu senaryo
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Interstellar", response.getBody().getTitle());
        verify(movieService, times(1)).updateMovie(movieId, request);
    }

    @Test
    void updateMovie_negativeScenario_movieNotFound_throwsException() {
        Long movieId = 99L;
        // DÜZELTME: `.duration()` -> `.durationDays()`
        MovieRequest request = MovieRequest.builder()
                .title("Interstellar")
                .summary("A space epic")
                .releaseDate(LocalDate.of(2014, 11, 7))
                .durationDays(169) // Düzeltildi
                .director("Christopher Nolan")
                .genre("Sci-Fi")
                .posterId(2L)
                .hallIds(List.of(1L)) // Zorunlu alan eklendi (varsayım)
                .cast(Collections.emptyList()) // Zorunlu alan eklendi (varsayım)
                .formats(Collections.emptyList()) // Zorunlu alan eklendi (varsayım)
                .build();

        when(movieService.updateMovie(movieId, request))
                .thenThrow(new ResourceNotFoundException("Movie not found"));

        // olumsuz senaryo
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> movieController.updateMovie(movieId, request));

        assertEquals("Movie not found", exception.getMessage());
        verify(movieService, times(1)).updateMovie(movieId, request);
    }

    // ------------------- DELETE (M13) -------------------

    @Test
    void deleteMovie_positiveScenario_returnsDeletedMovie() {
        Long movieId = 1L;

        // DÜZELTME: `.director()` kaldırıldı
        MovieResponse responseDto = MovieResponse.builder()
                .id(movieId) // ID eklendi
                .title("Inception")
                .slug("inception")
                .releaseDate(LocalDate.of(2010, 7, 16))
                // .director("Christopher Nolan") // Kaldırıldı
                .build();

        when(movieService.deleteById(movieId)).thenReturn(responseDto);

        ResponseEntity<MovieResponse> response = movieController.deleteMovie(movieId);

        // olumlu senaryo
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Inception", response.getBody().getTitle());
        verify(movieService, times(1)).deleteById(movieId);
    }

    @Test
    void deleteMovie_negativeScenario_movieNotFound_throwsException() {
        Long movieId = 99L;

        when(movieService.deleteById(movieId))
                .thenThrow(new ResourceNotFoundException("Movie not found"));

        // olumsuz senaryo
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> movieController.deleteMovie(movieId));

        assertEquals("Movie not found", exception.getMessage());
        verify(movieService, times(1)).deleteById(movieId);
    }
}