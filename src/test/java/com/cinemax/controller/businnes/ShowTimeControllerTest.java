package com.cinemax.controller.businnes;

import com.cinemax.payload.request.business.ShowTimeRequest;
import com.cinemax.payload.response.business.ShowTimeResponse;
import com.cinemax.service.bussines.ShowTimeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShowTimeControllerTest {

    @Mock
    private ShowTimeService showTimeService;

    @InjectMocks
    private ShowTimeController showTimeController;

    @Test
    void createShowTime_ok() {
        // given
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime start = LocalTime.of(10, 0);
        LocalTime end = LocalTime.of(12, 0);

        ShowTimeRequest request = ShowTimeRequest.builder()
                .date(date)
                .startTime(start)
                .endTime(end)
                .movieId(100L)
                .hallId(200L)
                .build();

        ShowTimeResponse expected = ShowTimeResponse.builder()
                .id(1L)
                .startDateTime(LocalDateTime.of(date, start))
                .endDateTime(LocalDateTime.of(date, end))
                .hallName("Hall A")
                .movieId(100L)
                .movieTitle("Inception")
                .build();

        when(showTimeService.createShowTime(request)).thenReturn(expected);

        // when
        ResponseEntity<ShowTimeResponse> resp = showTimeController.createShowTimeForMovie(request);

        // then
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals(expected, resp.getBody());

        verify(showTimeService).createShowTime(request); // times(1) varsayılan
        verifyNoMoreInteractions(showTimeService);
    }

    @Test
    void createMultipleShowTimes_ok() {
        // given
        LocalDate date = LocalDate.now().plusDays(2);

        ShowTimeRequest r1 = ShowTimeRequest.builder()
                .date(date)
                .startTime(LocalTime.of(13, 0))
                .endTime(LocalTime.of(15, 0))
                .movieId(10L)
                .hallId(20L)
                .build();

        ShowTimeRequest r2 = ShowTimeRequest.builder()
                .date(date)
                .startTime(LocalTime.of(16, 0))
                .endTime(LocalTime.of(18, 0))
                .movieId(10L)
                .hallId(21L)
                .build();

        List<ShowTimeRequest> requests = Arrays.asList(r1, r2);

        ShowTimeResponse s1 = ShowTimeResponse.builder()
                .id(11L)
                .startDateTime(LocalDateTime.of(date, r1.getStartTime()))
                .endDateTime(LocalDateTime.of(date, r1.getEndTime()))
                .hallName("Hall 20")
                .movieId(10L)
                .movieTitle("Matrix")
                .build();

        ShowTimeResponse s2 = ShowTimeResponse.builder()
                .id(12L)
                .startDateTime(LocalDateTime.of(date, r2.getStartTime()))
                .endDateTime(LocalDateTime.of(date, r2.getEndTime()))
                .hallName("Hall 21")
                .movieId(10L)
                .movieTitle("Matrix")
                .build();

        List<ShowTimeResponse> expectedList = Arrays.asList(s1, s2);
        when(showTimeService.createMultipleShowTimes(requests)).thenReturn(expectedList);

        // when
        ResponseEntity<List<ShowTimeResponse>> resp =
                showTimeController.createMultipleShowTimes(requests);

        // then
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals(expectedList, resp.getBody()); // Lombok @Data -> equals/hashCode

        verify(showTimeService).createMultipleShowTimes(requests);
        verifyNoMoreInteractions(showTimeService);
    }

    @Test
    void getShowTimeById_ok() {
        // given
        long id = 99L;
        LocalDate date = LocalDate.now().plusDays(3);
        LocalTime start = LocalTime.of(19, 30);
        LocalTime end = LocalTime.of(21, 30);

        ShowTimeResponse expected = ShowTimeResponse.builder()
                .id(id)
                .startDateTime(LocalDateTime.of(date, start))
                .endDateTime(LocalDateTime.of(date, end))
                .hallName("VIP Hall")
                .movieId(500L)
                .movieTitle("Interstellar")
                .build();

        when(showTimeService.getShowTimeById(id)).thenReturn(expected);

        // when
        ResponseEntity<ShowTimeResponse> resp = showTimeController.getShowTimeById(id);

        // then
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals(expected, resp.getBody());

        verify(showTimeService).getShowTimeById(id);
        verifyNoMoreInteractions(showTimeService);
    }

    @Test
    void getShowTimeById_propagatesException() {
        // given
        long missingId = 12345L;
        when(showTimeService.getShowTimeById(missingId))
                .thenThrow(new RuntimeException("ShowTime not found"));

        // when & then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> showTimeController.getShowTimeById(missingId));
        assertEquals("ShowTime not found", ex.getMessage());

        verify(showTimeService).getShowTimeById(missingId);
        verifyNoMoreInteractions(showTimeService);
    }
}
