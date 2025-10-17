package com.cinemax.service.businnes;

import com.cinemax.entity.concretes.business.City;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.mappers.CityMapper;
import com.cinemax.payload.request.business.CityRequest;
import com.cinemax.payload.response.business.CityResponse;
import com.cinemax.payload.response.business.CityWithCinemasResponse;
import com.cinemax.repository.businnes.CityRepository;
import com.cinemax.service.bussines.CityService;
import com.cinemax.service.helper.CityHelper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Mockito'yu JUnit 5 ile kullanmak için bu anotasyon gereklidir.


@ExtendWith(MockitoExtension.class)
public class CityServicesTest {
    //Ekrem

    @Mock
    private CityRepository cityRepository;
    @Mock
    private CityMapper cityMapper;
    @Mock
    private CityHelper cityHelper;

    // @InjectMocks: Yukarıda oluşturulan sahte nesneleri (mock'ları) CityService'in içine enjekte eder.

    @InjectMocks
    private CityService cityService;

    // Testlerde kullanılacak ortak nesneler
    private City city;
    private CityRequest cityRequest;
    private CityResponse cityResponse;
    private CityWithCinemasResponse cityWithCinemasResponse;

    // Her bir test metodu çalışmadan önce bu metot çalışır.
    // Testler için gerekli olan başlangıç nesnelerini oluşturur.

    @BeforeEach
    void setUp() {
        cityRequest = CityRequest.builder()
                .name("Ankara")
                .build();

        city = City.builder()
                .id(1L)
                .name("Ankara")
                .cinemas(Collections.emptyList())
                .build();

        cityResponse = CityResponse.builder()
                .id(1L)
                .name("Ankara")
                .build();

        cityWithCinemasResponse = CityWithCinemasResponse.builder()
                .id(1L)
                .name("Ankara")
                .cinemas(Collections.emptyList())
                .build();
    }

    // --- saveCity Metodu Testleri ---

    @Test
    void testSaveCity_Success() {
        // Arrange (Hazırlık): Metodun doğru çalışması için mock'ların davranışlarını belirliyoruz.
        // cityHelper.validateCityRequest çağrıldığında hiçbir şey yapma (hata fırlatma).
        doNothing().when(cityHelper).validateCityRequest(any(CityRequest.class));
        // Mapper'lar çağrıldığında beklenen nesneleri döndür.
        when(cityMapper.mapRequestToEntity(any(CityRequest.class))).thenReturn(city);
        when(cityRepository.save(any(City.class))).thenReturn(city);
        when(cityMapper.mapEntityToResponse(any(City.class))).thenReturn(cityResponse);

        // Act (Eylem): Asıl test edilecek metodu çağırıyoruz.
        CityResponse actualResponse = cityService.saveCity(cityRequest);

        // Assert (Doğrulama): Sonucun beklenen gibi olup olmadığını kontrol ediyoruz.
        assertNotNull(actualResponse);
        assertEquals("Ankara", actualResponse.getName());

        // Metotların doğru şekilde ve beklenen sayıda çağrıldığını doğruluyoruz.
        verify(cityHelper, times(1)).validateCityRequest(cityRequest);
        verify(cityRepository, times(1)).save(city);
    }

    // --- getCityWithCinemas Metodu Testleri ---

    @Test
    void testGetCityWithCinemas_Success() {
        // Arrange
        when(cityRepository.findById(1L)).thenReturn(Optional.of(city));
        when(cityMapper.mapToCityWithCinemasResponse(any(City.class))).thenReturn(cityWithCinemasResponse);

        // Act
        CityWithCinemasResponse actualResponse = cityService.getCityWithCinemas(1L);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(1L, actualResponse.getId());
        assertEquals("Ankara", actualResponse.getName());
    }

    @Test
    void testGetCityWithCinemas_ThrowsResourceNotFoundException_WhenCityNotFound() {
        // Arrange
        when(cityRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            cityService.getCityWithCinemas(99L);
        });
    }
    // --- deleteCity Metodu Testleri ---

    @Test
    void testDeleteCity_Success() {
        // Arrange
        when(cityRepository.findById(1L)).thenReturn(Optional.of(city));
        // cityRepository.delete metodu void olduğu için bir davranış belirtmeye gerek yok.

        // Act
        City deletedCity = cityService.deleteCity(1L);

        // Assert
        assertNotNull(deletedCity);
        assertEquals(1L, deletedCity.getId());
        // 'delete' metodunun 'city' nesnesi ile 1 kez çağrıldığını doğruluyoruz.
        verify(cityRepository, times(1)).delete(city);
    }

    @Test
    void testDeleteCity_ThrowsEntityNotFoundException_WhenCityNotFound() {
        // Arrange
        when(cityRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            cityService.deleteCity(99L);
        });
        // 'delete' metodunun hiç çağrılmadığını doğruluyoruz.
        verify(cityRepository, never()).delete(any());
    }
    // --- updateCity Metodu Testleri ---

    @Test
    void testUpdateCity_Success() {
        // Arrange
        CityRequest updateRequest = CityRequest.builder().name("İstanbul").build();
        when(cityRepository.findById(1L)).thenReturn(Optional.of(city));
        when(cityRepository.save(any(City.class))).thenReturn(city); // Güncellenmiş nesneyi döndürür
        when(cityMapper.mapEntityToResponse(any(City.class))).thenReturn(CityResponse.builder().id(1L).name("İstanbul").build());

        // Act
        CityResponse actualResponse = cityService.updateCity(1L, updateRequest);

        // Assert
        assertNotNull(actualResponse);
        assertEquals("İstanbul", actualResponse.getName());
        verify(cityMapper, times(1)).updateEntityFromRequest(city, updateRequest);
        verify(cityRepository, times(1)).save(city);
    }

    @Test
    void testUpdateCity_ThrowsResourceNotFoundException_WhenCityNotFound() {
        // Arrange
        when(cityRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            cityService.updateCity(99L, cityRequest);
        });
        verify(cityRepository, never()).save(any());
    }

    // --- getAllCitiesWithCinemas Metodu Testleri ---

    @Test
    void testGetAllCitiesWithCinemas_Success() {
        // Arrange
        List<City> cities = List.of(city);
        when(cityRepository.findAll()).thenReturn(cities);
        when(cityMapper.mapToCityWithCinemasResponse(any(City.class))).thenReturn(cityWithCinemasResponse);

        // Act
        List<CityWithCinemasResponse> actualResponse = cityService.getAllCitiesWithCinemas();

        // Assert
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.size());
        assertEquals("Ankara", actualResponse.get(0).getName());
    }

    @Test
    void testGetAllCitiesWithCinemas_ReturnsEmptyList_WhenNoCitiesExist() {
        // Arrange
        when(cityRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<CityWithCinemasResponse> actualResponse = cityService.getAllCitiesWithCinemas();

        // Assert
        assertNotNull(actualResponse);
        assertTrue(actualResponse.isEmpty());
    }
}

