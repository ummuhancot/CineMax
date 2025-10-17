package com.cinemax.service.businnes;


import com.cinemax.entity.concretes.business.Cinema;
import com.cinemax.entity.concretes.business.Hall;
import com.cinemax.entity.enums.HallType;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.mappers.HallMapper;
import com.cinemax.payload.request.business.HallRequest;
import com.cinemax.payload.response.business.HallResponse;
import com.cinemax.repository.businnes.CinemaRepository;
import com.cinemax.repository.businnes.HallRepository;
import com.cinemax.service.bussines.HallService;
import com.cinemax.service.helper.HallHelper;
import com.cinemax.service.validator.HallValidator;
import com.cinemax.util.HallSeatCache;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


// Mockito'yu JUnit 5 ile kullanmak için bu anotasyon gereklidir.
@ExtendWith(MockitoExtension.class)
public class HallServicesTest {
    //Ekrem

    // @Mock: Test edilen sınıfın bağımlılıklarını sahte (mock) nesnelerle değiştirir.
    @Mock
    private HallRepository hallRepository;
    @Mock
    private HallMapper hallMapper;
    @Mock
    private CinemaRepository cinemaRepository;

    // @InjectMocks: Yukarıda oluşturulan sahte nesneleri HallService'in içine enjekte eder.
    @InjectMocks
    private HallService hallService;

    // Statik metotları mock'lamak için MockedStatic nesneleri
    private MockedStatic<HallHelper> hallHelperMockedStatic;
    private MockedStatic<HallValidator> hallValidatorMockedStatic;
    private MockedStatic<HallSeatCache> hallSeatCacheMockedStatic;

    // Testlerde kullanılacak ortak nesneler
    private HallRequest hallRequest;
    private Hall hall;
    private Cinema cinema;
    private HallResponse hallResponse;

    // Her testten ÖNCE bu metot çalışır.
    @BeforeEach
    void setUp() {
        // Statik metotları mock'lamak için Mockito.mockStatic kullanılır.
        // Her testten önce bu mock'ları açıyoruz.
        hallHelperMockedStatic = Mockito.mockStatic(HallHelper.class);
        hallValidatorMockedStatic = Mockito.mockStatic(HallValidator.class);
        hallSeatCacheMockedStatic = Mockito.mockStatic(HallSeatCache.class);

        // Test için ortak nesneleri oluşturuyoruz.
        cinema = Cinema.builder().id(1L).name("Test Cinema").build();

        hallRequest = HallRequest.builder()
                .name("Salon A")
                .seatCapacity(100)
                .isSpecial(false)
                .type("STANDARD")
                .cinemaId(1L)
                .build();

        hall = Hall.builder()
                .id(1L)
                .name("Salon A")
                .seatCapacity(100)
                .isSpecial(false)
                .type(HallType.STANDARD)
                .cinema(cinema)
                .build();

        hallResponse = HallResponse.builder()
                .id(1L)
                .name("Salon A")
                .seatCapacity(100)
                .cinemaName("Test Cinema")
                .build();
    }
    // Her testten SONRA bu metot çalışır.
    @AfterEach
    void tearDown() {
        // Açılan statik mock'ları her testin sonunda kapatarak diğer testleri etkilemesini önleriz.
        hallHelperMockedStatic.close();
        hallValidatorMockedStatic.close();
        hallSeatCacheMockedStatic.close();
    }
    // --- saveHall Metodu Testleri ---
    @Test
    void testSaveHall_Success() {
        // Arrange (Hazırlık)
        // Statik metotların davranışlarını belirliyoruz.
        hallHelperMockedStatic.when(() -> HallHelper.findCinemaOrThrow(anyLong(), any(CinemaRepository.class)))
                .thenReturn(cinema);
        hallValidatorMockedStatic.when(() -> HallValidator.checkHallUnique(anyString(), anyLong(), any(HallRepository.class)))
                .then(invocation -> null); // Void metot için

        // Instance metotların davranışlarını belirliyoruz.
        when(hallMapper.convertRequestToHall(any(HallRequest.class), any(Cinema.class))).thenReturn(hall);
        when(hallRepository.save(any(Hall.class))).thenReturn(hall);
        when(hallMapper.convertHallToResponse(any(Hall.class))).thenReturn(hallResponse);

        // Act (Eylem)
        HallResponse response = hallService.saveHall(hallRequest);

        // Assert (Doğrulama)
        assertNotNull(response);
        assertEquals("Salon A", response.getName());
        verify(hallRepository, times(1)).save(hall); // save metodunun çağrıldığını doğrula
    }

    // --- getHallById Metodu Testleri ---

    @Test
    void testGetHallById_Success() {
        // Arrange
        when(hallRepository.findById(1L)).thenReturn(Optional.of(hall));
        when(hallMapper.convertHallToResponse(hall)).thenReturn(hallResponse);

        // Act
        HallResponse response = hallService.getHallById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
    }
    @Test
    void testGetHallById_ThrowsEntityNotFoundException_WhenHallNotFound() {
        // Arrange
        when(hallRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            hallService.getHallById(99L);
        });
    }

    // --- deleteHall Metodu Testleri ---

    @Test
    void testDeleteHall_Success() {
        // Arrange
        when(hallRepository.findById(1L)).thenReturn(Optional.of(hall));
        when(hallMapper.convertHallToResponse(hall)).thenReturn(hallResponse);

        // Act
        HallResponse response = hallService.deleteHall(1L);

        // Assert
        assertNotNull(response);
        // 'delete' metodunun doğru nesne ile çağrıldığını doğruluyoruz.
        verify(hallRepository, times(1)).delete(hall);
    }

    // --- getAllHalls Metodu Testleri ---

    @Test
    void testGetAllHalls_Success() {
        // Arrange
        when(hallRepository.findAll()).thenReturn(Collections.singletonList(hall));
        when(hallMapper.convertHallToResponse(hall)).thenReturn(hallResponse);

        // Act
        List<HallResponse> responses = hallService.getAllHalls();

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Salon A", responses.get(0).getName());
    }
    // --- updateHall Metodu Testleri ---

    @Test
    void testUpdateHall_Success() {
        // Arrange
        when(hallRepository.findById(1L)).thenReturn(Optional.of(hall));
        when(cinemaRepository.findById(1L)).thenReturn(Optional.of(cinema));
        when(hallMapper.updateHallFromRequest(any(Hall.class), any(HallRequest.class), any(Cinema.class))).thenReturn(hall);
        when(hallRepository.save(any(Hall.class))).thenReturn(hall);
        when(hallMapper.convertHallToResponse(any(Hall.class))).thenReturn(hallResponse);

        // Act
        HallResponse response = hallService.updateHall(1L, hallRequest);

        // Assert
        assertNotNull(response);
        assertEquals(hallResponse.getName(), response.getName());
        verify(hallRepository, times(1)).save(hall);
    }

    // --- getSeatsForHall Metodu Testleri ---

    @Test
    void testGetSeatsForHall_WhenCacheIsNotEmpty() {
        // Arrange
        List<String> cachedSeats = List.of("Seat-1", "Seat-2");
        hallSeatCacheMockedStatic.when(() -> HallSeatCache.hasHall(1L)).thenReturn(true);
        hallSeatCacheMockedStatic.when(() -> HallSeatCache.getSeats(1L)).thenReturn(cachedSeats);

        // Act
        List<String> seats = hallService.getSeatsForHall(1L);

        // Assert
        assertEquals(2, seats.size());
        // Cache'de olduğu için repository'nin çağrılmaması gerekir.
        verify(hallRepository, never()).findById(anyLong());
    }
    @Test
    void testGetSeatsForHall_WhenCacheIsEmptyAndHallExists() {
        // Arrange
        hall.setSeatCapacity(5); // Koltuk sayısını belirle
        List<String> generatedSeats = List.of("Seat-1", "Seat-2", "Seat-3", "Seat-4", "Seat-5");

        hallSeatCacheMockedStatic.when(() -> HallSeatCache.hasHall(1L)).thenReturn(false);
        when(hallRepository.findById(1L)).thenReturn(Optional.of(hall));
        // addSeatsToHall çağrıldıktan sonra getSeats'in dolu bir liste döndüreceğini varsayıyoruz.
        hallSeatCacheMockedStatic.when(() -> HallSeatCache.getSeats(1L)).thenReturn(generatedSeats);

        // Act
        List<String> seats = hallService.getSeatsForHall(1L);

        // Assert
        assertEquals(5, seats.size());
        verify(hallRepository, times(1)).findById(1L);
        // addSeatsToHall metodunun çağrıldığını dolaylı olarak doğruluyoruz.
        hallSeatCacheMockedStatic.verify(() -> HallSeatCache.addSeats(eq(1L), anyList()), times(1));
    }

    @Test
    void testGetSeatsForHall_ThrowsResourceNotFoundException_WhenHallDoesNotExist() {
        // Arrange
        hallSeatCacheMockedStatic.when(() -> HallSeatCache.hasHall(99L)).thenReturn(false);
        when(hallRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            hallService.getSeatsForHall(99L);
        });
    }
}

