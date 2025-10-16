package com.cinemax.controller.businnes;

import com.cinemax.entity.concretes.business.City;
import com.cinemax.payload.request.business.CityRequest;
import com.cinemax.payload.response.business.CityResponse;
import com.cinemax.payload.response.business.CityWithCinemasResponse;
import com.cinemax.service.bussines.CityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // openMocks/close gerektirmez
class CityControllerTest {

    @Mock
    private CityService cityService;

    @InjectMocks
    private CityController cityController;

    @Test
    void saveCity_returnsOkAndDelegatesToService() {
        CityRequest req = mock(CityRequest.class);
        CityResponse dto = mock(CityResponse.class);

        when(cityService.saveCity(req)).thenReturn(dto);

        ResponseEntity<CityResponse> res = cityController.saveCity(req);

        assertEquals(HttpStatus.OK, res.getStatusCode()); // getStatusCodeValue() yerine
        assertSame(dto, res.getBody());
        verify(cityService, times(1)).saveCity(req);
        verifyNoMoreInteractions(cityService);
    }

    @Test
    void getCityWithCinemas_returnsOkAndBody() {
        Long id = 1L;
        CityWithCinemasResponse dto = mock(CityWithCinemasResponse.class);

        when(cityService.getCityWithCinemas(id)).thenReturn(dto);

        ResponseEntity<CityWithCinemasResponse> res = cityController.getCityWithCinemas(id);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertSame(dto, res.getBody());
        verify(cityService, times(1)).getCityWithCinemas(id);
        verifyNoMoreInteractions(cityService);
    }

    @Test
    void deleteCity_returnsOkAndDeletedEntity() {
        Long id = 10L;
        City deleted = mock(City.class);

        when(cityService.deleteCity(id)).thenReturn(deleted);

        ResponseEntity<City> res = cityController.deleteCity(id);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertSame(deleted, res.getBody());
        verify(cityService, times(1)).deleteCity(id);
        verifyNoMoreInteractions(cityService);
    }

    @Test
    void updateCity_returnsOkAndUpdatedDto() {
        Long id = 2L;
        CityRequest req = mock(CityRequest.class);
        CityResponse dto = mock(CityResponse.class);

        when(cityService.updateCity(id, req)).thenReturn(dto);

        ResponseEntity<CityResponse> res = cityController.updateCity(id, req);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertSame(dto, res.getBody());
        verify(cityService, times(1)).updateCity(id, req);
        verifyNoMoreInteractions(cityService);
    }

    @Test
    void getAllCitiesWithCinemas_returnsOkAndList() {
        CityWithCinemasResponse dto1 = mock(CityWithCinemasResponse.class);
        CityWithCinemasResponse dto2 = mock(CityWithCinemasResponse.class);
        List<CityWithCinemasResponse> list = List.of(dto1, dto2);

        when(cityService.getAllCitiesWithCinemas()).thenReturn(list);

        ResponseEntity<List<CityWithCinemasResponse>> res = cityController.getAllCitiesWithCinemas();

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertSame(list, res.getBody());
        verify(cityService, times(1)).getAllCitiesWithCinemas();
        verifyNoMoreInteractions(cityService);
    }
}
