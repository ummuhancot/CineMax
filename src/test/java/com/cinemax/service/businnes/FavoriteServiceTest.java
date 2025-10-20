package com.cinemax.service.businnes;

import com.cinemax.controller.businnes.FavoriteController;
import com.cinemax.exception.ResourceNotFoundException; // Hata senaryosu için import
import com.cinemax.exception.UnauthorizedException; // Hata senaryosu için import
import com.cinemax.payload.request.business.FavoriteRequest;
import com.cinemax.payload.response.business.FavoriteResponse;
import com.cinemax.service.bussines.FavoriteService; // Test edilecek sınıfın bağımlılığı
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName; // Testlere açıklayıcı isimler vermek için
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension; // Mockito entegrasyonu
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FavoriteServiceTest {

    @Mock // Sahte (mock) bir FavoriteService nesnesi oluşturur
    private FavoriteService favoriteService;

    @InjectMocks // Mocklanan favoriteService nesnesini favoriteController'a enjekte eder
    private FavoriteController favoriteController;

    // Testlerde kullanılacak örnek değişkenler
    private Long userId;
    private Long favoriteId;
    private Long movieId;
    private Long cinemaId;
    private FavoriteRequest favoriteRequest;
    private FavoriteResponse favoriteResponse;
    private List<FavoriteResponse> favoriteResponseList;

    @BeforeEach
    void setUp() {
        // Her testten önce değişkenleri başlatır
        userId = 1L;
        favoriteId = 10L;
        movieId = 100L;
        cinemaId = 200L;

        // Geçerli bir istek nesnesi oluştur
        favoriteRequest = FavoriteRequest.builder()
                .movieId(movieId)
                .cinemaId(cinemaId)
                .build();

        // Geçerli bir yanıt nesnesi oluştur
        favoriteResponse = FavoriteResponse.builder()
                .userEmail("test@example.com")
                .movieTitle("Örnek Film")
                .cinemaName("Örnek Sinema")
                .addedAt(LocalDateTime.now())
                .build();

        // Geçerli bir yanıt listesi oluştur
        favoriteResponseList = Arrays.asList(favoriteResponse,
                FavoriteResponse.builder().userEmail("test2@example.com").movieTitle("Başka Film").build());
    }

    // --- addFavoriteMovie Testleri ---
    @Test
    @DisplayName("POST /{userId} - Favori Ekleme Başarılı")
    void addFavoriteMovie_whenValidRequest_shouldReturnCreated() {
        // Arrange (Hazırlık): Servis metodunun çağrıldığında ne döndüreceğini ayarla
        when(favoriteService.addMovieToFavorites(userId, favoriteRequest)).thenReturn(favoriteResponse);

        // Act (Eylem): Controller metodunu çağır
        ResponseEntity<FavoriteResponse> responseEntity = favoriteController.addFavoriteMovie(userId, favoriteRequest);

        // Assert (Doğrulama): HTTP durum kodunun 201 (Created) ve body'nin dolu olduğunu doğrula
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(favoriteResponse, responseEntity.getBody()); // Dönen body'nin beklenenle aynı olduğunu doğrula

        // Verify (Kontrol): Servis metodunun doğru argümanlarla 1 kez çağrıldığını doğrula
        verify(favoriteService, times(1)).addMovieToFavorites(userId, favoriteRequest);
        verifyNoMoreInteractions(favoriteService); // Başka bir servis metodu çağrılmadığından emin ol
    }

    // --- removeFavorite Testleri ---
    @Test
    @DisplayName("DELETE /{userId}/favorites/{favoriteId} - Favori Silme Başarılı")
    void removeFavorite_whenExists_shouldReturnOk() {
        // Arrange
        when(favoriteService.removeFavorite(userId, favoriteId)).thenReturn(favoriteResponse);

        // Act
        ResponseEntity<FavoriteResponse> responseEntity = favoriteController.removeFavorite(userId, favoriteId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(favoriteResponse, responseEntity.getBody());

        // Verify
        verify(favoriteService, times(1)).removeFavorite(userId, favoriteId);
        verifyNoMoreInteractions(favoriteService);
    }

    @Test
    @DisplayName("DELETE /{userId}/favorites/{favoriteId} - Favori Bulunamadı")
    void removeFavorite_whenNotFound_shouldThrowResourceNotFoundException() {
        // Arrange: Servisin exception fırlatmasını sağla
        when(favoriteService.removeFavorite(userId, favoriteId))
                .thenThrow(new ResourceNotFoundException("Favorite not found"));

        // Act & Assert: Belirtilen exception'ın fırlatıldığını doğrula
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            favoriteController.removeFavorite(userId, favoriteId);
        });

        assertEquals("Favorite not found", exception.getMessage()); // Exception mesajını kontrol et

        // Verify
        verify(favoriteService, times(1)).removeFavorite(userId, favoriteId);
        verifyNoMoreInteractions(favoriteService);
    }

    // --- getAllFavorites (Kullanıcıya göre) Testleri ---
    @Test
    @DisplayName("GET /{userId}/getAll - Kullanıcının Favorilerini Getirme Başarılı")
    void getAllFavorites_byUserId_shouldReturnOkWithList() {
        // Arrange
        when(favoriteService.getAllFavorites(userId)).thenReturn(favoriteResponseList);

        // Act
        ResponseEntity<List<FavoriteResponse>> responseEntity = favoriteController.getAllFavorites(userId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(2, responseEntity.getBody().size()); // Listenin boyutunu kontrol et
        assertEquals(favoriteResponseList, responseEntity.getBody()); // Listenin içeriğini kontrol et

        // Verify
        verify(favoriteService, times(1)).getAllFavorites(userId);
        verifyNoMoreInteractions(favoriteService);
    }

    // --- getAllFavorites (Tüm kullanıcılar) Testleri ---
    @Test
    @DisplayName("GET /getAll - Tüm Favorileri Getirme Başarılı")
    void getAllFavorites_forAllUsers_shouldReturnOkWithList() {
        // Arrange
        when(favoriteService.getAllFavoritesForAllUsers()).thenReturn(favoriteResponseList);

        // Act
        ResponseEntity<List<FavoriteResponse>> responseEntity = favoriteController.getAllFavorites();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(2, responseEntity.getBody().size());
        assertEquals(favoriteResponseList, responseEntity.getBody());

        // Verify
        verify(favoriteService, times(1)).getAllFavoritesForAllUsers();
        verifyNoMoreInteractions(favoriteService);
    }

    // --- getFavorite Testleri ---
    @Test
    @DisplayName("GET /{userId}/favorites/{favoriteId} - Tek Favori Getirme Başarılı")
    void getFavorite_whenExists_shouldReturnOk() {
        // Arrange
        when(favoriteService.getFavoriteById(userId, favoriteId)).thenReturn(favoriteResponse);

        // Act
        ResponseEntity<FavoriteResponse> responseEntity = favoriteController.getFavorite(userId, favoriteId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(favoriteResponse, responseEntity.getBody());

        // Verify
        verify(favoriteService, times(1)).getFavoriteById(userId, favoriteId);
        verifyNoMoreInteractions(favoriteService);
    }

    @Test
    @DisplayName("GET /{userId}/favorites/{favoriteId} - Favori Kullanıcıya Ait Değil")
    void getFavorite_whenNotOwnedByUser_shouldThrowUnauthorizedException() {
        // Arrange
        when(favoriteService.getFavoriteById(userId, favoriteId))
                .thenThrow(new UnauthorizedException("This favorite does not belong to the user"));

        // Act & Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            favoriteController.getFavorite(userId, favoriteId);
        });

        assertEquals("This favorite does not belong to the user", exception.getMessage());

        // Verify
        verify(favoriteService, times(1)).getFavoriteById(userId, favoriteId);
        verifyNoMoreInteractions(favoriteService);
    }
}

