package com.cinemax.service.businnes;


import com.cinemax.entity.concretes.business.Hall;
import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.entity.concretes.business.ShowTime;
import com.cinemax.exception.BadRequestException;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.mappers.ShowTimeMapper;
import com.cinemax.payload.request.business.ShowTimeRequest;
import com.cinemax.payload.response.business.ShowTimeResponse;
import com.cinemax.repository.businnes.ShowTimeRepository;
import com.cinemax.service.bussines.ShowTimeService;
import com.cinemax.service.helper.ShowTimeHelper;
import com.cinemax.service.validator.ShowTimeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShowTimeServiceTest {
    //Ekrem
    @Mock
    private ShowTimeRepository showTimeRepository;

    @Mock
    private ShowTimeHelper showTimeHelper;

    @Mock
    private ShowTimeMapper showTimeMapper;

    @Mock
    private ShowTimeValidator showTimeValidator;

    @InjectMocks
    private ShowTimeService showTimeService;

    // Testlerde kullanılacak ortak nesneler
    private ShowTimeRequest showTimeRequest;
    private Movie movie;
    private Hall hall;
    private ShowTime showTime;
    private ShowTimeResponse showTimeResponse;

    // Her bir test metodu çalışmadan önce bu metot çalışır.
    // Testler için gerekli olan başlangıç nesnelerini oluşturur.

    @BeforeEach
    void setUp() {
        movie = Movie.builder().id(1L).title("Test Movie").build();
        hall = Hall.builder().id(1L).name("Test Hall").build();

        showTimeRequest = ShowTimeRequest.builder()
                .movieId(1L)
                .hallId(1L)
                .date(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(16, 0))
                .build();

        showTime = ShowTime.builder()
                .id(1L)
                .movie(movie)
                .hall(hall)
                .date(showTimeRequest.getDate())
                .startTime(showTimeRequest.getStartTime())
                .endTime(showTimeRequest.getEndTime())
                .build();

        showTimeResponse = ShowTimeResponse.builder()
                .id(1L)
                .movieTitle("Test Movie")
                .hallName("Test Hall")
                .build();
    }
    // --- createShowTime Metodu Testleri ---

    @Test
    void testCreateShowTime_Success() {
        // Arrange (Hazırlık): Metodun doğru çalışması için mock'ların davranışlarını belirliyoruz.
        when(showTimeHelper.getMovieOrThrow(1L)).thenReturn(movie);
        when(showTimeHelper.getHallOrThrow(1L)).thenReturn(hall);
        doNothing().when(showTimeValidator).checkOverlap(anyLong(), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class));
        when(showTimeMapper.toEntity(any(ShowTimeRequest.class), any(Movie.class), any(Hall.class))).thenReturn(showTime);
        when(showTimeRepository.save(any(ShowTime.class))).thenReturn(showTime);
        when(showTimeMapper.toResponse(any(ShowTime.class))).thenReturn(showTimeResponse);

        // Act (Eylem): Asıl test edilecek metodu çağırıyoruz.
        ShowTimeResponse actualResponse = showTimeService.createShowTime(showTimeRequest);

        // Assert (Doğrulama): Sonucun beklenen gibi olup olmadığını kontrol ediyoruz.
        assertNotNull(actualResponse);
        assertEquals("Test Movie", actualResponse.getMovieTitle());
        assertEquals("Test Hall", actualResponse.getHallName());

        // Gerekli metotların doğru şekilde ve beklenen sayıda çağrıldığını doğruluyoruz.
        verify(showTimeHelper, times(1)).getMovieOrThrow(1L);
        verify(showTimeHelper, times(1)).getHallOrThrow(1L);
        verify(showTimeValidator, times(1)).checkOverlap(anyLong(), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class));
        verify(showTimeRepository, times(1)).save(showTime);
    }
    @Test
    void testCreateShowTime_ThrowsBadRequestException_WhenShowTimeOverlaps() {
        // Arrange
        when(showTimeHelper.getMovieOrThrow(1L)).thenReturn(movie);
        when(showTimeHelper.getHallOrThrow(1L)).thenReturn(hall);
        // Validator'ın bir hata fırlatacağını simüle ediyoruz.
        doThrow(new BadRequestException("Overlap detected")).when(showTimeValidator)
                .checkOverlap(anyLong(), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            showTimeService.createShowTime(showTimeRequest);
        });

        // Hata fırlatıldığı için save metodunun hiç çağrılmaması gerekir.
        verify(showTimeRepository, never()).save(any());
    }

    // --- createMultipleShowTimes Metodu Testleri ---

    @Test
    void testCreateMultipleShowTimes_Success() {
        // Arrange
        // createShowTime metodu zaten test edildiği için, burada sadece onun
        // doğru sayıda çağrıldığını ve sonuçları listeye eklediğini test edeceğiz.
        // Bu yüzden createShowTime metodunun davranışını mock'lamamıza gerek yok,
        // ancak bağımlılıklarının davranışlarını tanımlamamız gerekiyor.

        when(showTimeHelper.getMovieOrThrow(anyLong())).thenReturn(movie);
        when(showTimeHelper.getHallOrThrow(anyLong())).thenReturn(hall);
        when(showTimeMapper.toEntity(any(), any(), any())).thenReturn(showTime);
        when(showTimeRepository.save(any(ShowTime.class))).thenReturn(showTime);
        when(showTimeMapper.toResponse(any(ShowTime.class))).thenReturn(showTimeResponse);

        List<ShowTimeRequest> requests = List.of(showTimeRequest, showTimeRequest); // İki seans isteği

        // Act
        List<ShowTimeResponse> responses = showTimeService.createMultipleShowTimes(requests);

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size()); // İki istek için iki yanıt olmalı.
        // createShowTime içindeki her bir metodun 2 kez çağrıldığını doğruluyoruz.
        verify(showTimeRepository, times(2)).save(any(ShowTime.class));
        verify(showTimeValidator, times(2)).checkOverlap(anyLong(), any(), any(), any());
    }

    // --- getShowTimeById Metodu Testleri ---

    @Test
    void testGetShowTimeById_Success() {
        // Arrange
        when(showTimeRepository.findById(1L)).thenReturn(Optional.of(showTime));
        when(showTimeMapper.mapShowTimeToResponse(showTime)).thenReturn(showTimeResponse);

        // Act
        ShowTimeResponse actualResponse = showTimeService.getShowTimeById(1L);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(1L, actualResponse.getId());
    }

    @Test
    void testGetShowTimeById_ThrowsResourceNotFoundException_WhenShowTimeNotFound() {
        // Arrange
        when(showTimeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            showTimeService.getShowTimeById(99L);
        });
    }
}

