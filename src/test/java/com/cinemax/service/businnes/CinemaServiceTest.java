package com.cinemax.service.businnes;

import com.cinemax.entity.concretes.business.Cinema;
import com.cinemax.entity.concretes.business.City;
import com.cinemax.entity.concretes.business.Hall;
import com.cinemax.entity.enums.HallType;
import com.cinemax.exception.ConflictException;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.mappers.CinemaMapper;
import com.cinemax.payload.mappers.HallMapper;
import com.cinemax.payload.request.business.CinemaRequest;
import com.cinemax.payload.response.business.CinemaResponse;
import com.cinemax.payload.response.business.HallResponse;
import com.cinemax.repository.businnes.CinemaRepository;
import com.cinemax.repository.businnes.CityRepository;
import com.cinemax.repository.businnes.HallRepository;
import com.cinemax.service.bussines.CinemaService;
import com.cinemax.service.validator.UniquePropertyCinemaValidator;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CinemaServiceTest {

    // @Mock: Test edilen sınıfın bağımlılıklarını sahte (mock) nesnelerle değiştirir.
    // Bu sayede veritabanı gibi dış sistemlere bağlanmadan sadece servis mantığını test ederiz.
    @Mock
    private CinemaRepository cinemaRepository;
    @Mock
    private CinemaMapper cinemaMapper;
    @Mock
    private CityRepository cityRepository;
    @Mock
    private UniquePropertyCinemaValidator uniquePropertyCinemaValidator;
    @Mock
    private HallRepository hallRepository;
    @Mock
    private HallMapper hallMapper;

    // @InjectMocks: Yukarıda oluşturulan sahte nesneleri CinemaService'in içine enjekte eder.
    @InjectMocks
    private CinemaService cinemaService;

    // Testlerde kullanılacak ortak nesneleri burada tanımlıyoruz.
    private CinemaRequest cinemaRequest;
    private Cinema cinema;
    private City city;
    private CinemaResponse cinemaResponse;

    // Her test metodundan önce bu metot çalışır ve nesneleri yeniden oluşturur.
    @BeforeEach
    void setUp() {
        city = City.builder().id(1L).name("Istanbul").build();

        cinemaRequest = CinemaRequest.builder()
                .name("Test Cinema")
                .cityName("Istanbul")
                .address("Test Address")
                .phoneNumber("1234567890")
                .email("test@cinema.com")
                .build();

        cinema = Cinema.builder()
                .id(1L)
                .name("Test Cinema")
                .city(city)
                .slug("test-cinema-istanbul")
                .address("Test Address")
                .phoneNumber("1234567890")
                .email("test@cinema.com")
                .build();

        cinemaResponse = CinemaResponse.builder()
                .id(1L)
                .name("Test Cinema")
                .cityName("Istanbul")
                .build();
    }

    // --- createCinema Metodu Testleri ---

    @Test
    void testCreateCinema_Success() {
        // Arrange (Hazırlık): Metodun çalışması için gerekli olan sahte davranışları tanımlıyoruz.
        when(cityRepository.findByNameIgnoreCase("Istanbul")).thenReturn(Optional.of(city));
        doNothing().when(uniquePropertyCinemaValidator).validateUniqueEmailAndPhone(anyString(), anyString());
        when(cinemaMapper.generateSlug(anyString(), anyString())).thenReturn("test-cinema-istanbul");
        when(cinemaRepository.existsBySlug("test-cinema-istanbul")).thenReturn(false);
        when(cinemaMapper.convertRequestToCinema(cinemaRequest)).thenReturn(cinema);
        when(cinemaRepository.save(any(Cinema.class))).thenReturn(cinema);
        when(cinemaMapper.convertCinemaToResponse(cinema)).thenReturn(cinemaResponse);

        // Act (Eylem): Test edilecek metodu çağırıyoruz.
        CinemaResponse response = cinemaService.createCinema(cinemaRequest);

        // Assert (Doğrulama): Sonuçların beklendiği gibi olup olmadığını kontrol ediyoruz.
        assertNotNull(response);
        assertEquals("Test Cinema", response.getName());
        verify(cityRepository, times(1)).findByNameIgnoreCase(anyString());
        verify(cinemaRepository, times(1)).save(any(Cinema.class));
    }

    @Test
    void testCreateCinema_ThrowsResourceNotFoundException_WhenCityNotFound() {
        // Arrange
        when(cityRepository.findByNameIgnoreCase("Istanbul")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            cinemaService.createCinema(cinemaRequest);
        });

        // 'save' metodunun hiç çağrılmadığını doğruluyoruz.
        verify(cinemaRepository, never()).save(any());
    }

    @Test
    void testCreateCinema_ThrowsConflictException_WhenSlugAlreadyExists() {
        // Arrange
        when(cityRepository.findByNameIgnoreCase("Istanbul")).thenReturn(Optional.of(city));
        when(cinemaMapper.generateSlug(anyString(), anyString())).thenReturn("test-cinema-istanbul");
        when(cinemaRepository.existsBySlug("test-cinema-istanbul")).thenReturn(true);

        // Act & Assert
        assertThrows(ConflictException.class, () -> {
            cinemaService.createCinema(cinemaRequest);
        });
        verify(cinemaRepository, never()).save(any());
    }

    // --- deleteCinema Metodu Testleri ---

    @Test
    void testDeleteCinema_Success() {
        // Arrange
        when(cityRepository.existsById(1L)).thenReturn(true);
        when(cinemaRepository.findById(1L)).thenReturn(Optional.of(cinema));
        // delete metodu void olduğu için herhangi bir `when` tanımına gerek yok.

        // Act
        Cinema deletedCinema = cinemaService.deleteCinema(1L, 1L);

        // Assert
        assertNotNull(deletedCinema);
        assertEquals(1L, deletedCinema.getId());
        verify(cinemaRepository, times(1)).delete(cinema);
    }

    @Test
    void testDeleteCinema_ThrowsEntityNotFoundException_WhenCinemaNotInCity() {
        // Arrange
        City anotherCity = City.builder().id(99L).name("Ankara").build();
        cinema.setCity(anotherCity); // Sinemayı farklı bir şehre ata

        when(cityRepository.existsById(1L)).thenReturn(true);
        when(cinemaRepository.findById(1L)).thenReturn(Optional.of(cinema));

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            cinemaService.deleteCinema(1L, 1L); // Istanbul (cityId=1) için silmeye çalış
        });

        verify(cinemaRepository, never()).delete(any());
    }


    // --- getCinemaById Metodu Testleri ---

    @Test
    void testGetCinemaById_Success() {
        // Arrange
        when(cinemaRepository.findById(1L)).thenReturn(Optional.of(cinema));
        // Bu metot CinemaMapper kullanmıyor, doğrudan builder ile nesne oluşturuyor.
        // Bu yüzden mapper için bir 'when' tanımına gerek yok.

        // Act
        CinemaResponse response = cinemaService.getCinemaById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(cinema.getName(), response.getName());
        assertEquals(city.getName(), response.getCityName());
    }

    @Test
    void testGetCinemaById_ThrowsResourceNotFoundException_WhenCinemaDoesNotExist() {
        // Arrange
        when(cinemaRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            cinemaService.getCinemaById(1L);
        });
    }

    // --- getHallsByCinemaId Metodu Testleri ---

    @Test
    void testGetHallsByCinemaId_Success() {
        // Arrange
        Hall hall1 = Hall.builder().id(1L).name("Hall 1").build();
        HallResponse hallResponse1 = HallResponse.builder().id(1L).name("Hall 1").build();

        when(hallRepository.findByCinemaId(1L)).thenReturn(Collections.singletonList(hall1));
        when(hallMapper.convertHallToResponse(hall1)).thenReturn(hallResponse1);

        // Act
        List<HallResponse> responses = cinemaService.getHallsByCinemaId(1L);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Hall 1", responses.get(0).getName());
    }
}