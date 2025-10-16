//package com.cinemax.controller.user;
//
//import com.cinemax.controller.businnes.MovieController;
//import com.cinemax.exception.ResourceNotFoundException;
//import com.cinemax.payload.request.business.MovieRequest;
//import com.cinemax.payload.response.business.MovieResponse;
//import com.cinemax.payload.response.business.MovieShowTimesResponse;
//import com.cinemax.payload.response.business.ShowTimeResponse;
//import com.cinemax.service.bussines.MovieService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class MovieControllerTest {
//
//    @Mock
//    private MovieService movieService;
//
//    @InjectMocks
//    private MovieController movieController;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    // ------------------- SHOW TIMES -------------------
//
//    @Test
//    void getShowTimes_movieExists_returnsOk() {
//        Long movieId = 1L;
//
//        ShowTimeResponse showTimeResponse = new ShowTimeResponse();
//        showTimeResponse.setId(101L);
//
//        MovieShowTimesResponse responseDto = new MovieShowTimesResponse();
//        responseDto.setMovieId(movieId);
//        responseDto.setTitle("Inception");
//        responseDto.setShowTimes(List.of(showTimeResponse));
//
//        when(movieService.getUpcomingShowTimes(movieId)).thenReturn(responseDto);
//
//        ResponseEntity<MovieShowTimesResponse> response = movieController.getShowTimes(movieId);
//
//        assertNotNull(response);
//        assertEquals(200, response.getStatusCodeValue());
//        assertEquals(movieId, response.getBody().getMovieId());
//        assertEquals("Inception", response.getBody().getTitle());
//        assertEquals(101L, response.getBody().getShowTimes().get(0).getId());
//
//        verify(movieService, times(1)).getUpcomingShowTimes(movieId);
//    }
//
//    @Test
//    void getShowTimes_movieNotFound_throwsException() {
//        Long movieId = 99L;
//
//        when(movieService.getUpcomingShowTimes(movieId))
//                .thenThrow(new ResourceNotFoundException("Movie not found"));
//
//        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
//                () -> movieController.getShowTimes(movieId));
//
//        assertEquals("Movie not found", thrown.getMessage());
//        verify(movieService, times(1)).getUpcomingShowTimes(movieId);
//    }
//
//    // ------------------- SAVE -------------------
//
//    @Test
//    void saveMovie_positiveScenario_returnsCreatedMovie() {
//        MovieRequest request = MovieRequest.builder()
//                .title("Inception")
//                .summary("A mind-bending thriller")
//                .releaseDate(LocalDate.of(2010, 7, 16))
//                .duration(148)
//                .rating(8.8)
//                .director("Christopher Nolan")
//                .genre("Sci-Fi")
//                .posterId(1L)
//                .build();
//
//        MovieResponse responseDto = MovieResponse.builder()
//                .title("Inception")
//                .slug("inception")
//                .summary("A mind-bending thriller")
//                .releaseDate(LocalDate.of(2010, 7, 16))
//                .duration(148)
//                .rating(8.8)
//                .director("Christopher Nolan")
//                .genre("Sci-Fi")
//                .build();
//
//        when(movieService .save(request)).thenReturn(responseDto);
//
//        ResponseEntity<MovieResponse> response = movieController.movieSave(request);
//
//        // olumlu senaryo
//        assertNotNull(response);
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertEquals("Inception", response.getBody().getTitle());
//        assertEquals("Christopher Nolan", response.getBody().getDirector());
//        verify(movieService, times(1)).save(request);
//    }
//
//    @Test
//    void saveMovie_negativeScenario_serviceThrowsException() {
//        MovieRequest request = MovieRequest.builder()
//                .title("Inception")
//                .summary("A mind-bending thriller")
//                .releaseDate(LocalDate.of(2010, 7, 16))
//                .duration(148)
//                .director("Christopher Nolan")
//                .genre("Sci-Fi")
//                .posterId(1L)
//                .build();
//
//        when(movieService.save(request)).thenThrow(new RuntimeException("Save failed"));
//
//        // olumsuz senaryo
//        RuntimeException exception = assertThrows(RuntimeException.class,
//                () -> movieController.movieSave(request));
//
//        assertEquals("Save failed", exception.getMessage());
//        verify(movieService, times(1)).save(request);
//    }
//
//    // ------------------- UPDATE -------------------
//
//    @Test
//    void updateMovie_positiveScenario_returnsUpdatedMovie() {
//        Long movieId = 1L;
//        MovieRequest request = MovieRequest.builder()
//                .title("Interstellar")
//                .summary("A space epic")
//                .releaseDate(LocalDate.of(2014, 11, 7))
//                .duration(169)
//                .director("Christopher Nolan")
//                .genre("Sci-Fi")
//                .posterId(2L)
//                .build();
//
//        MovieResponse responseDto = MovieResponse.builder()
//                .title("Interstellar")
//                .slug("interstellar")
//                .releaseDate(LocalDate.of(2014, 11, 7))
//                .director("Christopher Nolan")
//                .build();
//
//        when(movieService.updateMovie(movieId, request)).thenReturn(responseDto);
//
//        ResponseEntity<MovieResponse> response = movieController.updateMovie(movieId, request);
//
//        // olumlu senaryo
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Interstellar", response.getBody().getTitle());
//        verify(movieService, times(1)).updateMovie(movieId, request);
//    }
//
//    @Test
//    void updateMovie_negativeScenario_movieNotFound_throwsException() {
//        Long movieId = 99L;
//        MovieRequest request = MovieRequest.builder()
//                .title("Interstellar")
//                .summary("A space epic")
//                .releaseDate(LocalDate.of(2014, 11, 7))
//                .duration(169)
//                .director("Christopher Nolan")
//                .genre("Sci-Fi")
//                .posterId(2L)
//                .build();
//
//        when(movieService.updateMovie(movieId, request))
//                .thenThrow(new ResourceNotFoundException("Movie not found"));
//
//        // olumsuz senaryo
//        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
//                () -> movieController.updateMovie(movieId, request));
//
//        assertEquals("Movie not found", exception.getMessage());
//        verify(movieService, times(1)).updateMovie(movieId, request);
//    }
//
//    // ------------------- DELETE -------------------
//
//    @Test
//    void deleteMovie_positiveScenario_returnsDeletedMovie() {
//        Long movieId = 1L;
//
//        MovieResponse responseDto = MovieResponse.builder()
//                .title("Inception")
//                .slug("inception")
//                .releaseDate(LocalDate.of(2010, 7, 16))
//                .director("Christopher Nolan")
//                .build();
//
//        when(movieService.deleteById(movieId)).thenReturn(responseDto);
//
//        ResponseEntity<MovieResponse> response = movieController.deleteMovie(movieId);
//
//        // olumlu senaryo
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Inception", response.getBody().getTitle());
//        verify(movieService, times(1)).deleteById(movieId);
//    }
//
//    @Test
//    void deleteMovie_negativeScenario_movieNotFound_throwsException() {
//        Long movieId = 99L;
//
//        when(movieService.deleteById(movieId))
//                .thenThrow(new ResourceNotFoundException("Movie not found"));
//
//        // olumsuz senaryo
//        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
//                () -> movieController.deleteMovie(movieId));
//
//        assertEquals("Movie not found", exception.getMessage());
//        verify(movieService, times(1)).deleteById(movieId);
//    }
//}
