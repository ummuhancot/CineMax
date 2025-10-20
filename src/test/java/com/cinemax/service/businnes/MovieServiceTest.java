package com.cinemax.service.businnes;
import com.cinemax.entity.concretes.business.*;
import com.cinemax.entity.enums.MovieStatus;
import com.cinemax.exception.BadRequestException;
import com.cinemax.exception.ConflictException;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.mappers.MovieAdminMapper;
import com.cinemax.payload.mappers.MovieMapper;
import com.cinemax.payload.mappers.MovieShowTimesMapper;
import com.cinemax.payload.mappers.ShowTimeMapper;
import com.cinemax.payload.request.business.MovieRequest;
import com.cinemax.payload.messages.ErrorMessages;
import com.cinemax.payload.response.business.MovieResponse;
import com.cinemax.payload.response.business.MovieShowTimesResponse;

import com.cinemax.repository.businnes.HallRepository;
import com.cinemax.repository.businnes.ImageRepository;
import com.cinemax.repository.businnes.MovieRepository;
import com.cinemax.repository.businnes.ShowTimeRepository;
import com.cinemax.service.bussines.MovieService;
import com.cinemax.service.bussines.ShowTimeService;
import com.cinemax.service.helper.MovieHelper;
import com.cinemax.service.validator.MovieValidator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    // Mocklanacak Bağımlılıklar
    @Mock private MovieRepository movieRepository;
    @Mock private ImageRepository imageRepository; // Image işlemleri için
    @Mock private ShowTimeRepository showTimeRepository;
    @Mock private MovieMapper movieMapper;
    @Mock private MovieShowTimesMapper movieShowTimesMapper;
    @Mock private MovieHelper movieHelper;
    @Mock private HallRepository hallRepository; // Hall işlemleri için
    @Mock private ShowTimeMapper showTimeMapper; // Lazım olabilir
    @Mock private MovieAdminMapper movieAdminMapper; // Lazım olabilir
    @Mock private ShowTimeService showTimeService; // Lazım olabilir
    @Mock private MovieValidator movieValidator;

    // Test Edilecek Sınıf
    @InjectMocks private MovieService movieService;

    // Testlerde kullanılacak ortak nesneler
    private MovieRequest movieRequest;
    private Movie movie;
    private MovieResponse movieResponse;
    private Hall hall;
    private Image poster;
    private List<Hall> halls;

    @BeforeEach
    void setUp() {
        hall = Hall.builder()
                .id(1L)
                .name("Salon 1")
                .cinema(Cinema.builder().id(1L).name("Test Cinema").city(City.builder().id(1L).name("Test City").build()).build()) // Slug üretimi için gerekli
                .build();
        halls = List.of(hall);

        poster = Image.builder().id(1L).name("poster.jpg").build();

        movieRequest = MovieRequest.builder()
                .title("Inception")
                .summary("A mind-bending thriller")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .durationDays(148) // duration -> durationDays olarak düzeltildi
                .rating(8.8)
                .director("Christopher Nolan")
                .genre("Sci-Fi")
                .posterId(1L)
                .cast(List.of("Leonardo DiCaprio", "Joseph Gordon-Levitt"))
                .formats(List.of("IMAX", "Standard"))
                .hallIds(List.of(1L)) // Hall ID'leri eklendi
                .status(MovieStatus.IN_THEATERS)
                .build();

        movie = Movie.builder()
                .id(1L)
                .title("Inception")
                .slug("test-cinema-test-city-salon-1-inception") // Örnek slug
                .summary("A mind-bending thriller")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .durationDays(148)
                .rating(8.8)
                .director("Christopher Nolan")
                .genre("Sci-Fi")
                .poster(poster)
                .halls(new ArrayList<>(halls)) // ArrayList'e çevrildi
                .cast(new ArrayList<>(List.of("Leonardo DiCaprio")))
                .formats(new ArrayList<>(List.of("IMAX")))
                .status(MovieStatus.IN_THEATERS)
                .build();

        movieResponse = MovieResponse.builder()
                .id(1L)
                .title("Inception")
                .slug("test-cinema-test-city-salon-1-inception")
                .summary("A mind-bending thriller")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .duration(148) // Response'da duration olabilir, kontrol edin
                .rating(8.8)
                .genre("Sci-Fi")
                .halls(List.of("Salon 1"))
                .status(MovieStatus.IN_THEATERS)
                .build();
    }

    // ------------------- saveMovie -------------------

    @Test
    void saveMovie_Success() {
        // --- Arrange ---
        // Statik metodu mocklamak için try-with-resources
        try (MockedStatic<MovieValidator> validatorMockedStatic = Mockito.mockStatic(MovieValidator.class)) {
            // Statik metot çağrıldığında ne döneceğini tanımla
            validatorMockedStatic.when(() -> MovieValidator.generateUniqueSlug(any(MovieRequest.class), anyList(), any(MovieRepository.class)))
                    .thenReturn("generated-unique-slug");

            // Diğer bağımlılıkların davranışlarını tanımla
            when(movieHelper.getHallsOrThrow(movieRequest.getHallIds())).thenReturn(halls);
            doNothing().when(movieValidator).validateUniqueMovieInHalls(anyString(), any(Hall.class)); // void metot
            when(movieMapper.mapMovieRequestToMovie(eq(movieRequest), eq(halls), eq("generated-unique-slug"))) // Argümanlar güncellendi
                    .thenReturn(movie);
            when(movieRepository.save(movie)).thenReturn(movie);
            when(movieMapper.mapMovieToMovieResponse(movie)).thenReturn(movieResponse);

            // --- Act ---
            MovieResponse result = movieService.saveMovie(movieRequest);

            // --- Assert ---
            assertNotNull(result);
            assertEquals("Inception", result.getTitle());
            assertEquals("generated-unique-slug", result.getSlug()); // Üretilen slug kontrolü
            verify(movieHelper, times(1)).getHallsOrThrow(anyList());
            verify(movieValidator, times(halls.size())).validateUniqueMovieInHalls(eq("generated-unique-slug"), any(Hall.class));
            verify(movieMapper, times(1)).mapMovieRequestToMovie(eq(movieRequest), eq(halls), eq("generated-unique-slug"));
            verify(movieRepository, times(1)).save(movie);
            verify(movieMapper, times(1)).mapMovieToMovieResponse(movie);
        } // Statik mock bloğu burada kapanır
    }

    @Test
    void saveMovie_HallNotFound_ThrowsResourceNotFoundException() {
        when(movieHelper.getHallsOrThrow(movieRequest.getHallIds())).thenThrow(new ResourceNotFoundException(ErrorMessages.HALL_NOT_FOUND));

        assertThrows(ResourceNotFoundException.class, () -> movieService.saveMovie(movieRequest));

        verify(movieRepository, never()).save(any()); // Save çağrılmamalı
    }

    @Test
    void saveMovie_MovieAlreadyExistsInHall_ThrowsIllegalArgumentException() {
        try (MockedStatic<MovieValidator> validatorMockedStatic = Mockito.mockStatic(MovieValidator.class)) {
            validatorMockedStatic.when(() -> MovieValidator.generateUniqueSlug(any(MovieRequest.class), anyList(), any(MovieRepository.class)))
                    .thenReturn("existing-slug");

            when(movieHelper.getHallsOrThrow(movieRequest.getHallIds())).thenReturn(halls);
            // İlk salon için hata fırlatmasını sağla
            doThrow(new IllegalArgumentException("Film zaten salonda mevcut"))
                    .when(movieValidator).validateUniqueMovieInHalls(eq("existing-slug"), eq(hall));

            assertThrows(IllegalArgumentException.class, () -> movieService.saveMovie(movieRequest));

            verify(movieRepository, never()).save(any());
        }
    }


    // ------------------- updateMovie -------------------

    @Test
    void updateMovie_Success() {
        // --- Arrange ---
        Long movieId = 1L;
        MovieRequest updateRequest = MovieRequest.builder()
                .title("Inception Updated")
                .summary("Updated Summary")
                .posterId(2L) // Farklı poster ID
                .hallIds(List.of(1L)) // Aynı salon ID'leri
                // Diğer güncellenecek alanlar...
                .durationDays(150)
                .build();

        Image updatedPoster = Image.builder().id(2L).name("poster2.jpg").build();
        Movie existingMovie = movie; // setUp'ta oluşturulanı kullan

        // MovieHelper mockları
        when(movieHelper.getMovieOrThrow(movieId)).thenReturn(existingMovie);
        when(movieHelper.getHallsOrThrow(updateRequest.getHallIds())).thenReturn(halls);
        when(movieHelper.getPosterOrThrow(updateRequest.getPosterId())).thenReturn(updatedPoster);

        // Mapper mock (update metodu void olduğu için doNothing)
        // Argüman sayısı 4 oldu
        doNothing().when(movieMapper).updateMovieFromRequest(eq(existingMovie), eq(updateRequest), eq(halls), eq(updatedPoster));

        // Repository save mock
        when(movieRepository.save(existingMovie)).thenReturn(existingMovie); // Güncellenmiş movie döner

        // Mapper response mock
        MovieResponse updatedResponse = MovieResponse.builder()
                .id(movieId)
                .title("Inception Updated")
                //... diğer alanlar
                .build();
        when(movieMapper.mapMovieToMovieResponse(existingMovie)).thenReturn(updatedResponse);

        // --- Act ---
        MovieResponse result = movieService.updateMovie(movieId, updateRequest);

        // --- Assert ---
        assertNotNull(result);
        assertEquals("Inception Updated", result.getTitle());
        verify(movieHelper, times(1)).getMovieOrThrow(movieId);
        verify(movieHelper, times(1)).getHallsOrThrow(updateRequest.getHallIds());
        verify(movieHelper, times(1)).getPosterOrThrow(updateRequest.getPosterId());
        verify(movieMapper, times(1)).updateMovieFromRequest(eq(existingMovie), eq(updateRequest), eq(halls), eq(updatedPoster));
        verify(movieRepository, times(1)).save(existingMovie);
        verify(movieMapper, times(1)).mapMovieToMovieResponse(existingMovie);
    }

    @Test
    void updateMovie_MovieNotFound_ThrowsResourceNotFoundException() {
        Long movieId = 99L;
        when(movieHelper.getMovieOrThrow(movieId)).thenThrow(new ResourceNotFoundException("Movie not found"));

        assertThrows(ResourceNotFoundException.class, () -> movieService.updateMovie(movieId, movieRequest));

        verify(movieRepository, never()).save(any());
    }

    @Test
    void updateMovie_PosterNotFound_ThrowsResourceNotFoundException() {
        Long movieId = 1L;
        movieRequest.setPosterId(99L); // Var olmayan poster ID
        when(movieHelper.getMovieOrThrow(movieId)).thenReturn(movie);
        when(movieHelper.getHallsOrThrow(movieRequest.getHallIds())).thenReturn(halls);
        when(movieHelper.getPosterOrThrow(99L)).thenThrow(new ResourceNotFoundException("Poster not found"));

        assertThrows(ResourceNotFoundException.class, () -> movieService.updateMovie(movieId, movieRequest));

        verify(movieRepository, never()).save(any());
    }


    // ------------------- deleteById -------------------

    @Test
    void deleteById_Success() {
        Long movieId = 1L;
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(movieMapper.mapMovieToMovieResponse(movie)).thenReturn(movieResponse);
        doNothing().when(movieRepository).delete(movie); // void metot
        doNothing().when(movieRepository).flush();       // void metot

        MovieResponse result = movieService.deleteById(movieId);

        assertNotNull(result);
        assertEquals(movieResponse.getTitle(), result.getTitle());
        verify(movieRepository, times(1)).findById(movieId);
        verify(movieMapper, times(1)).mapMovieToMovieResponse(movie);
        verify(movieRepository, times(1)).delete(movie);
        verify(movieRepository, times(1)).flush();
    }

    @Test
    void deleteById_MovieNotFound_ThrowsResourceNotFoundException() {
        Long movieId = 99L;
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> movieService.deleteById(movieId));

        verify(movieRepository, times(1)).findById(movieId);
        verify(movieMapper, never()).mapMovieToMovieResponse(any());
        verify(movieRepository, never()).delete(any());
        verify(movieRepository, never()).flush();
    }


    // ------------------- getUpcomingShowTimes -------------------

    @Test
    void getUpcomingShowTimes_Success() {
        Long movieId = 1L;
        ShowTime st1 = ShowTime.builder().id(10L).date(LocalDate.now()).startTime(LocalTime.now().plusHours(1)).build();
        ShowTime st2 = ShowTime.builder().id(11L).date(LocalDate.now().plusDays(1)).startTime(LocalTime.of(10,0)).build();
        List<ShowTime> showTimes = List.of(st1, st2);
        MovieShowTimesResponse expectedResponse = new MovieShowTimesResponse(); // İçini doldurmaya gerek yok, sadece mapper'ın döndürdüğünü test ediyoruz

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(showTimeRepository.findUpcomingShowTimes(eq(movieId), any(LocalDate.class), any(LocalTime.class)))
                .thenReturn(showTimes);
        when(movieShowTimesMapper.mapMovieWithShowTimesToResponse(movie, showTimes)).thenReturn(expectedResponse);

        MovieShowTimesResponse result = movieService.getUpcomingShowTimes(movieId);

        assertNotNull(result);
        assertSame(expectedResponse, result); // Mapper'ın döndürdüğü nesne ile aynı mı?
        verify(movieRepository, times(1)).findById(movieId);
        verify(showTimeRepository, times(1)).findUpcomingShowTimes(eq(movieId), any(LocalDate.class), any(LocalTime.class));
        verify(movieShowTimesMapper, times(1)).mapMovieWithShowTimesToResponse(movie, showTimes);
    }

    @Test
    void getUpcomingShowTimes_MovieNotFound_ThrowsResourceNotFoundException() {
        Long movieId = 99L;
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> movieService.getUpcomingShowTimes(movieId));

        verify(showTimeRepository, never()).findUpcomingShowTimes(anyLong(), any(), any());
        verify(movieShowTimesMapper, never()).mapMovieWithShowTimesToResponse(any(), any());
    }


    // ------------------- Diğer Metotlar İçin Testler (getMoviesByHall, getMoviesInTheaters vb.) -------------------
    // Bu metotlar genellikle repository çağrısı ve mapper dönüşümü içerir, benzer şekilde test edilebilirler.
    // Örnek: getMoviesByHall
    @Test
    void getMoviesByHall_Success() {
        String hallName = "Salon 1";
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "title"));
        List<Movie> moviesFromRepo = List.of(movie);
        Page<Movie> moviePage = new PageImpl<>(moviesFromRepo, pageable, 1);

        when(movieRepository.findByHalls_Name(hallName, pageable)).thenReturn(moviePage);
        when(movieMapper.mapMovieToMovieResponse(movie)).thenReturn(movieResponse);

        List<MovieResponse> result = movieService.getMoviesByHall(hallName, 0, 10, "title", "ASC");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(movieResponse.getTitle(), result.get(0).getTitle());
        verify(movieRepository, times(1)).findByHalls_Name(hallName, pageable);
        verify(movieMapper, times(1)).mapMovieToMovieResponse(movie);
    }

    // TODO: getMoviesInTheaters, getMoviesInTheatersWithDateCheck, getComingSoon, searchMovies, getMovieById, getMovieByIdAdmin, getAllMovies, saveMovies için testler eklenebilir.

}