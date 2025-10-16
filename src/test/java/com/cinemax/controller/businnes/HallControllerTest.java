package com.cinemax.controller.businnes;

import com.cinemax.payload.request.business.HallRequest;
import com.cinemax.payload.response.business.HallResponse;
import com.cinemax.service.bussines.HallService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * HallController için unit test sınıfı.
 * DTO sınıflarındaki @SuperBuilder anotasyonu kullanılarak nesneler oluşturulmuş
 * ve DTO'lardaki gerçek alan adları (name, seatCapacity) kullanılarak düzeltilmiştir.
 */
@ExtendWith(MockitoExtension.class)
class HallControllerTest {

    @Mock
    private HallService hallService;

    @InjectMocks
    private HallController hallController;

    private HallRequest hallRequest;
    private HallResponse hallResponse;

    @BeforeEach
    void setUp() {
        // HATA DÜZELTMESİ: Nesneler, DTO'lardaki @SuperBuilder'a uygun olarak
        // builder() metodu ve doğru alan adları kullanılarak oluşturuluyor.
        // HallRequest sınıfınızın da builder'a sahip olduğu varsayılmıştır.
        hallRequest = HallRequest.builder()
                .name("Salon 5") // 'hallNumber' yerine 'name' kullanıldı
                .seatCapacity(150) // 'capacity' yerine 'seatCapacity' kullanıldı
                .build();

        hallResponse = HallResponse.builder()
                .id(1L)
                .name("Salon 5") // 'hallNumber' yerine 'name' kullanıldı
                .seatCapacity(150) // 'capacity' yerine 'seatCapacity' kullanıldı
                .cinemaName("Cinemaximum")
                .isSpecial(false)
                .type("2D")
                .build();
    }

    @Test
    void testSaveHall_ShouldReturnCreatedResponse() {
        when(hallService.saveHall(any(HallRequest.class))).thenReturn(hallResponse);

        ResponseEntity<HallResponse> actualResponse = hallController.saveHall(hallRequest);

        assertNotNull(actualResponse);
        assertEquals(HttpStatus.CREATED, actualResponse.getStatusCode());
        assertEquals(hallResponse, actualResponse.getBody());
        verify(hallService, times(1)).saveHall(any(HallRequest.class));
    }

    @Test
    void testGetHallById_ShouldReturnHallResponse() {
        Long hallId = 1L;
        when(hallService.getHallById(hallId)).thenReturn(hallResponse);

        ResponseEntity<HallResponse> actualResponse = hallController.getHallById(hallId);

        assertNotNull(actualResponse);
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals(hallResponse, actualResponse.getBody());
        verify(hallService, times(1)).getHallById(hallId);
    }

    @Test
    void testDeleteHall_ShouldReturnDeletedHallResponse() {
        Long hallId = 1L;
        when(hallService.deleteHall(hallId)).thenReturn(hallResponse);

        ResponseEntity<HallResponse> actualResponse = hallController.deleteHall(hallId);

        assertNotNull(actualResponse);
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals(hallResponse, actualResponse.getBody());
        verify(hallService, times(1)).deleteHall(hallId);
    }

    @Test
    void testGetAllHalls_ShouldReturnListOfHalls() {
        List<HallResponse> hallList = Collections.singletonList(hallResponse);
        when(hallService.getAllHalls()).thenReturn(hallList);

        ResponseEntity<List<HallResponse>> actualResponse = hallController.getAllHalls();

        assertNotNull(actualResponse);
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertNotNull(actualResponse.getBody());
        assertEquals(1, actualResponse.getBody().size());
        assertEquals(hallList, actualResponse.getBody());
        verify(hallService, times(1)).getAllHalls();
    }

    @Test
    void testUpdateHall_ShouldReturnUpdatedHallResponse() {
        Long hallId = 1L;

        // HATA DÜZELTMESİ: Nesneler builder ve doğru alan adları ile oluşturuluyor.
        HallRequest updateRequest = HallRequest.builder()
                .name("Yeni Salon 5")
                .seatCapacity(200)
                .build();

        HallResponse updatedResponse = HallResponse.builder()
                .id(hallId)
                .name("Yeni Salon 5")
                .seatCapacity(200)
                .build();

        when(hallService.updateHall(hallId, updateRequest)).thenReturn(updatedResponse);

        ResponseEntity<HallResponse> actualResponse = hallController.updateHall(hallId, updateRequest);

        assertNotNull(actualResponse);
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals(updatedResponse, actualResponse.getBody());
        verify(hallService, times(1)).updateHall(hallId, updateRequest);
    }

    @Test
    void testGetHallSeats_ShouldReturnListOfSeats() {
        Long hallId = 1L;
        List<String> seats = List.of("A1", "A2", "A3");
        when(hallService.getSeatsForHall(hallId)).thenReturn(seats);

        ResponseEntity<List<String>> actualResponse = hallController.getHallSeats(hallId);

        assertNotNull(actualResponse);
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertNotNull(actualResponse.getBody());
        assertEquals(3, actualResponse.getBody().size());
        assertEquals(seats, actualResponse.getBody());
        verify(hallService, times(1)).getSeatsForHall(hallId);
    }
}