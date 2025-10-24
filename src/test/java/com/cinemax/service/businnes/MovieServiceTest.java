package com.cinemax.service.businnes; // Paket adını projenize göre doğrulayın

import com.cinemax.entity.concretes.business.Cinema;
import com.cinemax.entity.concretes.business.Hall;
import com.cinemax.entity.concretes.business.Image;
import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.entity.concretes.business.ShowTime;
import com.cinemax.exception.ConflictException; // ConflictException import edildi
import com.cinemax.exception.ImageException;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.mappers.MovieMapper;
import com.cinemax.payload.mappers.MovieShowTimesMapper;
import com.cinemax.payload.messages.ErrorMessages; // ErrorMessages import edildi
import com.cinemax.payload.response.business.MovieResponse;
import com.cinemax.payload.response.business.MovieShowTimesResponse;
// HallRepository importu kaldırıldı (kullanılmıyor)
import com.cinemax.repository.businnes.ImageRepository;
import com.cinemax.repository.businnes.MovieRepository;
import com.cinemax.repository.businnes.ShowTimeRepository;
import com.cinemax.payload.request.business.MovieRequest;
import com.cinemax.service.bussines.MovieService; // Service sınıfının doğru import edildiğinden emin olun
import com.cinemax.service.helper.MovieHelper;
import com.cinemax.service.validator.MovieValidator; // MovieValidator import edildi
import org.junit.jupiter.api.AfterEach; // AfterEach import edildi
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime; // LocalTime import edildi
import java.util.Collections;
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
    @Mock
    private MovieHelper movieHelper;
    // @Mock private HallRepository hallRepository; // DÜZELTME: Kullanılmadığı için kaldırıldı
    @Mock
    private MovieValidator movieValidator; // DÜZELTME: MovieValidator mock olarak eklendi

    @InjectMocks
    private MovieService movieService;

    // MovieValidator statik metotlarını mocklamak için
    private MockedStatic<MovieValidator> movieValidatorStaticMocked;

    @BeforeEach
    void setup() {
        movieValidatorStaticMocked = Mockito.mockStatic(MovieValidator.class);
    }

    @AfterEach
    void tearDown() {
        movieValidatorStaticMocked.close();
    }

    // --- GET UPCOMING SHOW TIMES ---

    @Test
    void getUpcomingShowTimes_existingMovie_returnsDto() {
        Long movieId = 1L;
        Movie movie = Movie.builder().id(movieId).title("Inception").build();
        ShowTime showTime = ShowTime.builder().id(101L).build();

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(showTimeRepository.findUpcomingShowTimes(eq(movieId), any(LocalDate.class), any(LocalTime.class)))
                .thenReturn(List.of(showTime));
        when(movieShowTimesMapper.mapMovieWithShowTimesToResponse(movie, List.of(showTime)))
                .thenReturn(MovieShowTimesResponse.builder().movieId(movieId).title("Inception").showTimes(Collections.emptyList()).build());

        MovieShowTimesResponse result = movieService.getUpcomingShowTimes(movieId);

        assertNotNull(result);
        assertEquals(movieId, result.getMovieId());
        assertEquals("Inception", result.getTitle());

        verify(movieRepository).findById(movieId);
        verify(showTimeRepository).findUpcomingShowTimes(eq(movieId), any(LocalDate.class), any(LocalTime.class));
        verify(movieShowTimesMapper).mapMovieWithShowTimesToResponse(movie, List.of(showTime));
    }

    @Test
    void getUpcomingShowTimes_movieNotFound_throwsException() {
        Long movieId = 99L;
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> movieService.getUpcomingShowTimes(movieId));

        assertEquals("Movie with id " + movieId + " not found", exception.getMessage());
        verify(movieRepository).findById(movieId);
        verifyNoInteractions(showTimeRepository, movieShowTimesMapper);
    }

    // --- SAVE ---

    @Test
    void saveMovie_positiveScenario_returnsMovieResponse() {
        MovieRequest request = MovieRequest.builder()
                .title("Inception")
                .summary("A mind-bending thriller")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .durationDays(148)
                .director("Christopher Nolan")
                .genre("Sci-Fi")
                .hallIds(List.of(1L))
                .cast(List.of("Leonardo DiCaprio"))
                .formats(List.of("IMAX"))
                .build();

        Hall mockHall = Hall.builder().id(1L).name("Salon 1").cinema(Cinema.builder().name("Mock Cinema").build()).build();
        List<Hall> mockHalls = List.of(mockHall);
        String mockSlug = "mock-cinema-salon-1-inception";

        Movie movieEntity = Movie.builder().title("Inception").slug(mockSlug).halls(mockHalls).build();
        MovieResponse responseDto = MovieResponse.builder().id(10L).title("Inception").slug(mockSlug).build();

        when(movieHelper.getHallsOrThrow(request.getHallIds())).thenReturn(mockHalls);
        movieValidatorStaticMocked.when(() -> MovieValidator.generateUniqueSlug(eq(request), eq(mockHalls), eq(movieRepository)))
                .thenReturn(mockSlug);
        doNothing().when(movieValidator).validateUniqueMovieInHalls(eq(mockSlug), eq(mockHall));
        when(movieMapper.mapMovieRequestToMovie(eq(request), eq(mockHalls), eq(mockSlug))).thenReturn(movieEntity);
        when(movieRepository.save(movieEntity)).thenReturn(movieEntity);
        when(movieMapper.mapMovieToMovieResponse(movieEntity)).thenReturn(responseDto);

        MovieResponse response = movieService.saveMovie(request);

        assertNotNull(response);
        assertEquals("Inception", response.getTitle());
        assertEquals(mockSlug, response.getSlug());

        verify(movieHelper).getHallsOrThrow(request.getHallIds());
        movieValidatorStaticMocked.verify(() -> MovieValidator.generateUniqueSlug(eq(request), eq(mockHalls), eq(movieRepository)));
        verify(movieValidator).validateUniqueMovieInHalls(eq(mockSlug), eq(mockHall));
        verify(movieMapper).mapMovieRequestToMovie(eq(request), eq(mockHalls), eq(mockSlug));
        verify(movieRepository).save(movieEntity);
        verify(movieMapper).mapMovieToMovieResponse(movieEntity);
        verifyNoMoreInteractions(movieRepository, movieMapper, movieHelper, imageRepository, showTimeRepository, movieShowTimesMapper);
        verifyNoMoreInteractions(movieValidator);
    }

    @Test
    void saveMovie_negativeScenario_hallNotFound_throwsException() {
        MovieRequest request = MovieRequest.builder()
                .title("Inception")
                .hallIds(List.of(99L))
                .cast(Collections.emptyList())
                .formats(Collections.emptyList())
                .durationDays(120)
                .releaseDate(LocalDate.now())
                .summary("summary")
                .director("director")
                .genre("genre")
                .build();

        // DÜZELTME: Helper'dan ResourceNotFoundException fırlatılacak, ErrorMessages kullanılacak
        when(movieHelper.getHallsOrThrow(request.getHallIds()))
                .thenThrow(new ResourceNotFoundException(ErrorMessages.HALL_NOT_FOUND)); // Direkt mesajı kullandık

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> movieService.saveMovie(request));

        // DÜZELTME: Beklenen mesaj formatlama içermiyor
        assertEquals(ErrorMessages.HALL_NOT_FOUND, exception.getMessage());
        verify(movieHelper).getHallsOrThrow(request.getHallIds());
        verify(movieRepository, never()).save(any());
        movieValidatorStaticMocked.verifyNoInteractions();
        verifyNoInteractions(movieValidator, movieMapper, imageRepository);
    }

    // ------------------- UPDATE ---

    @Test
    void updateMovie_positiveScenario_returnsUpdatedMovie() {
        Long movieId = 1L;
        MovieRequest request = MovieRequest.builder()
                .title("Interstellar")
                .posterId(2L)
                .hallIds(List.of(1L))
                .cast(Collections.emptyList())
                .formats(Collections.emptyList())
                .durationDays(169)
                .releaseDate(LocalDate.now())
                .summary("summary")
                .director("director")
                .genre("genre")
                .build();

        Movie existingMovie = Movie.builder().id(movieId).slug("interstellar-old").build();
        Image poster = Image.builder().id(2L).build();
        Hall mockHall = Hall.builder().id(1L).name("Salon 1").build();
        List<Hall> mockHalls = List.of(mockHall);
        MovieResponse responseDto = MovieResponse.builder().id(movieId).title("Interstellar").build();

        when(movieHelper.getMovieOrThrow(movieId)).thenReturn(existingMovie);
        when(movieHelper.getHallsOrThrow(request.getHallIds())).thenReturn(mockHalls);
        // Poster null değilse getPosterOrThrow çağrılır
        when(movieHelper.getPosterOrThrow(request.getPosterId())).thenReturn(poster);
        doNothing().when(movieMapper).updateMovieFromRequest(eq(existingMovie), eq(request), eq(mockHalls), eq(poster));
        when(movieRepository.save(existingMovie)).thenReturn(existingMovie);
        when(movieMapper.mapMovieToMovieResponse(existingMovie)).thenReturn(responseDto);

        MovieResponse result = movieService.updateMovie(movieId, request);

        assertNotNull(result);
        assertEquals("Interstellar", result.getTitle());

        verify(movieHelper).getMovieOrThrow(movieId);
        verify(movieHelper).getHallsOrThrow(request.getHallIds());
        verify(movieHelper).getPosterOrThrow(request.getPosterId());
        verify(movieMapper).updateMovieFromRequest(eq(existingMovie), eq(request), eq(mockHalls), eq(poster));
        verify(movieRepository).save(existingMovie);
        verify(movieMapper).mapMovieToMovieResponse(existingMovie);
        verifyNoMoreInteractions(movieRepository, movieMapper, movieHelper, imageRepository);
        verifyNoInteractions(movieValidator);
        movieValidatorStaticMocked.verifyNoInteractions();
    }

    @Test
    void updateMovie_negativeScenario_movieNotFound_throwsException() {
        Long movieId = 99L;
        MovieRequest request = MovieRequest.builder()
                .title("Interstellar")
                .posterId(2L)
                .hallIds(List.of(1L))
                .cast(Collections.emptyList())
                .formats(Collections.emptyList())
                .durationDays(169)
                .releaseDate(LocalDate.now())
                .summary("summary")
                .director("director")
                .genre("genre")
                .build();

        // DÜZELTME: MovieHelper'dan exception fırlatılacak, ErrorMessages kullanılacak
        when(movieHelper.getMovieOrThrow(movieId))
                .thenThrow(new ResourceNotFoundException("Movie not found with id: " + movieId)); // Helper'daki mesaj bu

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> movieService.updateMovie(movieId, request));

        // DÜZELTME: Mesajı kontrol et
        assertEquals("Movie not found with id: " + movieId, exception.getMessage()); // Formatlı mesaj bekleniyor
        verify(movieHelper).getMovieOrThrow(movieId);
        verify(movieRepository, never()).save(any());
        verify(movieHelper, never()).getHallsOrThrow(anyList());
        verify(movieHelper, never()).getPosterOrThrow(anyLong());
        verifyNoInteractions(movieMapper, imageRepository, movieValidator);
        movieValidatorStaticMocked.verifyNoInteractions();
    }

    // ------------------- DELETE ---

    @Test
    void deleteMovie_positiveScenario_returnsMovie() {
        Long movieId = 1L;
        Movie movie = Movie.builder().id(movieId).title("Inception").build();
        MovieResponse responseDto = MovieResponse.builder().id(movieId).title("Inception").build();

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(movieMapper.mapMovieToMovieResponse(movie)).thenReturn(responseDto);

        MovieResponse result = movieService.deleteById(movieId);

        assertNotNull(result);
        assertEquals("Inception", result.getTitle());

        verify(movieRepository).findById(movieId);
        verify(movieMapper).mapMovieToMovieResponse(movie);
        verify(movieRepository).delete(movie);
        verify(movieRepository).flush();
        verifyNoMoreInteractions(movieRepository, movieMapper);
        verifyNoInteractions(movieHelper, movieValidator);
        movieValidatorStaticMocked.verifyNoInteractions();
    }

    @Test
    void deleteMovie_negativeScenario_movieNotFound_throwsException() {
        Long movieId = 99L;
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> movieService.deleteById(movieId));

        // DÜZELTME: Service'deki mesajla eşleşmeli (ID eklenmiş hali)
        assertEquals(ErrorMessages.MOVIE_DELETE_FAILED + movieId, exception.getMessage());
        verify(movieRepository).findById(movieId);
        verify(movieRepository, never()).delete(any());
        verify(movieRepository, never()).flush();
        verifyNoInteractions(movieMapper, movieHelper, movieValidator);
        movieValidatorStaticMocked.verifyNoInteractions();
    }
}