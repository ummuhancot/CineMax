package com.cinemax.service.businnes;

import com.cinemax.entity.concretes.business.Image;
import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.entity.concretes.business.ShowTime;
import com.cinemax.exception.ConflictException;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.mappers.MovieMapper;
import com.cinemax.payload.mappers.MovieShowTimesMapper;
import com.cinemax.payload.response.business.MovieResponse;
import com.cinemax.payload.response.business.MovieShowTimesResponse;
import com.cinemax.repository.businnes.ImageRepository;
import com.cinemax.repository.businnes.MovieRepository;
import com.cinemax.repository.businnes.ShowTimeRepository;
import com.cinemax.payload.request.business.MovieRequest;
import com.cinemax.service.bussines.MovieService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private ShowTimeRepository showTimeRepository;

    @Mock
    private MovieShowTimesMapper movieShowTimesMapper;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private MovieMapper movieMapper;

    @InjectMocks
    private MovieService movieService;

    // ------------------- GET UPCOMING SHOW TIMES -------------------

    @Test
    void getUpcomingShowTimes_existingMovie_returnsDto() {
        Long movieId = 1L;
        Movie movie = new Movie();
        movie.setId(movieId);
        movie.setTitle("Inception");

        ShowTime showTime = new ShowTime();
        showTime.setId(101L);

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(showTimeRepository.findUpcomingShowTimes(anyLong(), any(), any())).thenReturn(List.of(showTime));
        when(movieShowTimesMapper.mapMovieWithShowTimesToResponse(movie, List.of(showTime)))
                .thenReturn(new MovieShowTimesResponse());

        MovieShowTimesResponse result = movieService.getUpcomingShowTimes(movieId);

        assertNotNull(result);
        verify(movieRepository).findById(movieId);
        verify(showTimeRepository).findUpcomingShowTimes(anyLong(), any(), any());
        verify(movieShowTimesMapper).mapMovieWithShowTimesToResponse(movie, List.of(showTime));
    }

    @Test
    void getUpcomingShowTimes_movieNotFound_throwsException() {
        Long movieId = 99L;
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> movieService.getUpcomingShowTimes(movieId));

        assertTrue(exception.getMessage().contains("not found"));
        verify(movieRepository).findById(movieId);
    }

    // ------------------- SAVE -------------------

    @Test
    void saveMovie_positiveScenario_returnsMovieResponse() {
        MovieRequest request = MovieRequest.builder()
                .title("Inception")
                .summary("A mind-bending thriller")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .duration(148)
                .director("Christopher Nolan")
                .genre("Sci-Fi")
                .posterId(1L)
                .build();

        Image poster = new Image();
        poster.setId(1L);

        Movie movieEntity = new Movie();
        movieEntity.setTitle("Inception");

        MovieResponse responseDto = MovieResponse.builder()
                .title("Inception")
                .build();

        when(imageRepository.findById(1L)).thenReturn(Optional.of(poster));
        when(movieRepository.existsBySlug(anyString())).thenReturn(false);
        when(movieMapper.mapMovieRequestToMovie(request)).thenReturn(movieEntity);
        when(movieRepository.save(movieEntity)).thenReturn(movieEntity);
        when(movieMapper.mapMovieToMovieResponse(movieEntity)).thenReturn(responseDto);

        MovieResponse response = movieService.save(request);

        assertNotNull(response);
        assertEquals("Inception", response.getTitle());
        verify(movieRepository).save(movieEntity);
    }

    @Test
    void saveMovie_negativeScenario_slugExists_throwsException() {
        MovieRequest request = MovieRequest.builder().title("Inception").posterId(1L).build();

        when(movieRepository.existsBySlug(anyString())).thenReturn(true);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> movieService.save(request));

        assertTrue(exception.getMessage().contains("Slug already exists"));
        verify(movieRepository, never()).save(any());
    }

    @Test
    void saveMovie_negativeScenario_posterNotFound_throwsException() {
        MovieRequest request = MovieRequest.builder().title("Inception").posterId(1L).build();
        when(movieRepository.existsBySlug(anyString())).thenReturn(false);
        when(imageRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> movieService.save(request));

        assertTrue(exception.getMessage().contains("Poster not found"));
        verify(movieRepository, never()).save(any());
    }

    // ------------------- UPDATE -------------------

    @Test
    void updateMovie_positiveScenario_returnsUpdatedMovie() {
        Long movieId = 1L;
        MovieRequest request = MovieRequest.builder().title("Interstellar").posterId(2L).build();
        Movie existingMovie = new Movie();
        existingMovie.setId(movieId);
        existingMovie.setSlug("interstellar-old");

        Image poster = new Image();
        poster.setId(2L);

        MovieResponse responseDto = MovieResponse.builder().title("Interstellar").build();

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(existingMovie));
        when(movieRepository.existsBySlug(anyString())).thenReturn(false);
        when(imageRepository.findById(2L)).thenReturn(Optional.of(poster));
        doNothing().when(movieMapper).updateMovieFromRequest(existingMovie, request);
        when(movieRepository.save(existingMovie)).thenReturn(existingMovie);
        when(movieMapper.mapMovieToMovieResponse(existingMovie)).thenReturn(responseDto);

        MovieResponse result = movieService.updateMovie(movieId, request);

        assertNotNull(result);
        assertEquals("Interstellar", result.getTitle());
        verify(movieRepository).save(existingMovie);
    }

    @Test
    void updateMovie_negativeScenario_movieNotFound_throwsException() {
        Long movieId = 99L;
        MovieRequest request = MovieRequest.builder().title("Interstellar").posterId(2L).build();

        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> movieService.updateMovie(movieId, request));

        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void updateMovie_negativeScenario_slugConflict_throwsException() {
        Long movieId = 1L;
        MovieRequest request = MovieRequest.builder().title("Interstellar").posterId(2L).build();
        Movie existingMovie = new Movie();
        existingMovie.setId(movieId);
        existingMovie.setSlug("old-slug");

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(existingMovie));
        when(movieRepository.existsBySlug(anyString())).thenReturn(true);

        ConflictException exception = assertThrows(ConflictException.class,
                () -> movieService.updateMovie(movieId, request));

        assertTrue(exception.getMessage().contains("already exists"));
    }

    // ------------------- DELETE -------------------

    @Test
    void deleteMovie_positiveScenario_returnsMovie() {
        Long movieId = 1L;
        Movie movie = new Movie();
        movie.setId(movieId);
        movie.setTitle("Inception");

        MovieResponse responseDto = MovieResponse.builder().title("Inception").build();

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(movieMapper.mapMovieToMovieResponse(movie)).thenReturn(responseDto);

        MovieResponse result = movieService.deleteById(movieId);

        assertNotNull(result);
        assertEquals("Inception", result.getTitle());
        verify(movieRepository).findById(movieId);
    }

    @Test
    void deleteMovie_negativeScenario_movieNotFound_throwsException() {
        Long movieId = 99L;
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> movieService.deleteById(movieId));

        assertTrue(exception.getMessage().contains("Failed"));
    }
}
