// package com.cinemax.controller.user;

//import com.cinemax.controller.businnes.CinemaController;
//import com.cinemax.entity.concretes.business.Cinema;
//import com.cinemax.exception.ResourceNotFoundException;
//import com.cinemax.payload.request.business.CinemaRequest;
//import com.cinemax.payload.response.business.CinemaHallResponse;
//import com.cinemax.payload.response.business.CinemaResponse;
//import com.cinemax.payload.response.business.HallResponse;
//import com.cinemax.service.bussines.CinemaService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class CinemaControllerTest {
//
//    @Mock
//    private CinemaService cinemaService;
//
//    @InjectMocks
//    private CinemaController cinemaController;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    // ------------------- GET /city-hall -------------------
//
//    @Test
//    void getCinemas_whenCalled_returnsList() {
//        String city = "Istanbul";
//        String specialHall = "IMAX";
//
//        CinemaHallResponse r1 = new CinemaHallResponse();
//        CinemaHallResponse r2 = new CinemaHallResponse();
//        List<CinemaHallResponse> expected = List.of(r1, r2);
//
//        when(cinemaService.getCinemas(city, specialHall)).thenReturn(expected);
//
//        List<CinemaHallResponse> actual = cinemaController.getCinemas(city, specialHall);
//
//        assertNotNull(actual);
//        assertEquals(2, actual.size());
//        verify(cinemaService, times(1)).getCinemas(city, specialHall);
//    }
//
//    @Test
//    void deleteCinema_whenExists_returnsOkWithBody() {
//        Long cityId = 34L, cinemaId = 100L;
//        Cinema deleted = new Cinema();
//
//        when(cinemaService.deleteCinema(cityId, cinemaId)).thenReturn(deleted);
//
//        ResponseEntity<Cinema> entity = cinemaController.deleteCinema(cityId, cinemaId);
//
//        assertEquals(HttpStatus.OK, entity.getStatusCode());
//        assertSame(deleted, entity.getBody());
//        verify(cinemaService, times(1)).deleteCinema(cityId, cinemaId);
//    }
//
//    // ------------------- POST /save -------------------
//
//    @Test
//    void createCinema_whenValid_returnsCreatedResponse() {
//        CinemaRequest request = new CinemaRequest();
//        CinemaResponse response = new CinemaResponse();
//
//        when(cinemaService.createCinema(request)).thenReturn(response);
//
//        ResponseEntity<CinemaResponse> entity = cinemaController.createCinema(request);
//
//        assertNotNull(entity);
//        assertEquals(HttpStatus.CREATED, entity.getStatusCode());
//        assertSame(response, entity.getBody());
//        verify(cinemaService, times(1)).createCinema(request);
//    }
//
//    // ------------------- DELETE /{cityId}/auth/{cinemaId} -------------------
//
//    @Test
//    void deleteCinema_WhenExist_returnsOkWithBody() {
//        Long cityId = 34L, cinemaId = 100L;
//        Cinema deleted = new Cinema();
//
//        when(cinemaService.deleteCinema(cityId, cinemaId)).thenReturn(deleted);
//
//        ResponseEntity<Cinema> entity = cinemaController.deleteCinema(cityId, cinemaId);
//
//        assertEquals(HttpStatus.OK, entity.getStatusCode());
//        assertSame(deleted, entity.getBody());
//        verify(cinemaService, times(1)).deleteCinema(cityId, cinemaId);
//    }
//
//    // ------------------- PUT /update/{id} -------------------
//
//    @Test
//    void updateCinema_whenValid_returnOk() {
//        Long id = 10L;
//        CinemaRequest request = new CinemaRequest();
//        CinemaResponse response = new CinemaResponse();
//
//        when(cinemaService.updateCinema(id, request)).thenReturn(response);
//
//        ResponseEntity<CinemaResponse> entity = cinemaController.updateCinema(id, request);
//
//        assertEquals(HttpStatus.OK, entity.getStatusCode());
//        assertSame(response, entity.getBody());
//        verify(cinemaService, times(1)).updateCinema(id, request);
//    }
//
//    // ------------------- GET /{id} -------------------
//
//    @Test
//    void getCinemaById_whenExist_returnOk() {
//        Long id = 5L;
//        CinemaResponse response = new CinemaResponse();
//
//        when(cinemaService.getCinemaById(id)).thenReturn(response);
//
//        ResponseEntity<CinemaResponse> entity = cinemaController.getCinemaById(id);
//
//        assertEquals(HttpStatus.OK, entity.getStatusCode());
//        assertSame(response, entity.getBody());
//        verify(cinemaService, times(1)).getCinemaById(id);
//    }
//
//    @Test
//    void getCinemaById_whenNotFound_throwException() {
//        Long id = 404L;
//        when(cinemaService.getCinemaById(id)).thenThrow(new ResourceNotFoundException("Cinema not found"));
//
//        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
//                () -> cinemaController.getCinemaById(id));
//
//        assertEquals("Cinema not found", thrown.getMessage());
//        verify(cinemaService, times(1)).getCinemaById(id);
//    }
//
//    // ------------------- GET /{id}/halls -------------------
//
//    @Test
//    void getHallsByCinema_whenExists_returnList() {
//        Long cinemaId = 7L;
//        List<HallResponse> expected = List.of(new HallResponse(), new HallResponse());
//
//        when(cinemaService.getHallsByCinemaId(cinemaId)).thenReturn(expected);
//
//        List<HallResponse> actual = cinemaController.getHallsByCinema(cinemaId);
//
//        assertNotNull(actual);
//        assertEquals(2, actual.size());
//        verify(cinemaService, times(1)).getHallsByCinemaId(cinemaId);
//    }
//
//    // ------------------- GET /specialhalls/{cinemaId} -------------------
//
//    @Test
//    void getSpecialHallsByCinema_whenExists_returnList() {
//        Long cinemaId = 11L;
//        List<HallResponse> expected = List.of(new HallResponse());
//
//        when(cinemaService.getSpecialHallsByCinemaId(cinemaId)).thenReturn(expected);
//
//        List<HallResponse> actual = cinemaController.getSpecialHallsByCinema(cinemaId);
//
//        assertNotNull(actual);
//        assertEquals(1, actual.size());
//        verify(cinemaService, times(1)).getSpecialHallsByCinemaId(cinemaId);
//    }
//}
