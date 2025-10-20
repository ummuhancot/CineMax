package com.cinemax.controller.businnes; // Controller ile aynı pakette olması önerilir

import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.request.business.FavoriteRequest;
import com.cinemax.payload.response.business.FavoriteResponse;
import com.cinemax.service.bussines.FavoriteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Mockito entegrasyonu için
class FavoriteControllerTest {

    @Mock // Test edilecek sınıfın bağımlılığını mock'luyoruz
    private FavoriteService favoriteService;

    @InjectMocks // Mock'lanan bağımlılıkları controller'a enjekte ediyoruz
    private FavoriteController favoriteController;

    // Testlerde kullanılacak örnek veriler
    private Long userId;
    private Long favoriteId;
    private Long movieId;
    private Long cinemaId;
    private FavoriteRequest favoriteRequest;
    private FavoriteResponse favoriteResponse;

    @BeforeEach
    void setUp() {
        // MockitoAnnotations.openMocks(this); // @ExtendWith varsa buna gerek yok
        // Her testten önce örnek verileri ayarla
        userId = 1L;
        favoriteId = 10L;
        movieId = 100L;
        cinemaId = 200L;

        favoriteRequest = FavoriteRequest.builder()
                .movieId(movieId)
                .cinemaId(cinemaId)
                .build();

        favoriteResponse = FavoriteResponse.builder()
                .userEmail("user@example.com")
                .movieTitle("Test Movie")
                .cinemaName("Test Cinema")
                .addedAt(LocalDateTime.now())
                .build();
    }

    // --- Test for addFavoriteMovie (POST /{userId}) ---
    @Test
    void addFavoriteMovie_whenValidRequest_shouldReturnCreated() {
        // Arrange: Servis çağrıldığında ne döneceğini tanımla
        when(favoriteService.addMovieToFavorites(userId, favoriteRequest)).thenReturn(favoriteResponse);

        // Act: Controller metodunu çağır
        ResponseEntity<FavoriteResponse> responseEntity = favoriteController.addFavoriteMovie(userId, favoriteRequest);

        // Assert: Dönen HTTP durumunu ve body'yi kontrol et
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(favoriteResponse.getMovieTitle(), responseEntity.getBody().getMovieTitle());
        assertEquals(favoriteResponse.getCinemaName(), responseEntity.getBody().getCinemaName());

        // Verify: Servis metodunun doğru parametrelerle 1 kez çağrıldığını doğrula
        verify(favoriteService, times(1)).addMovieToFavorites(userId, favoriteRequest);
        verifyNoMoreInteractions(favoriteService); // Başka servis çağrısı olmadığından emin ol
    }

    // --- Test for removeFavorite (DELETE /{userId}/favorites/{favoriteId}) ---
    @Test
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

    // --- Test for getAllFavorites for a specific user (GET /{userId}/getAll) ---
    @Test
    void getAllFavorites_byUserId_shouldReturnOkWithList() {
        // Arrange
        List<FavoriteResponse> expectedFavorites = Arrays.asList(favoriteResponse,
                FavoriteResponse.builder().movieTitle("Another Movie").build());
        when(favoriteService.getAllFavorites(userId)).thenReturn(expectedFavorites);

        // Act
        ResponseEntity<List<FavoriteResponse>> responseEntity = favoriteController.getAllFavorites(userId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(2, responseEntity.getBody().size());
        assertEquals(expectedFavorites, responseEntity.getBody());

        // Verify
        verify(favoriteService, times(1)).getAllFavorites(userId);
        verifyNoMoreInteractions(favoriteService);
    }

    // --- Test for getAllFavorites for all users (GET /getAll) ---
    @Test
    void getAllFavorites_forAllUsers_shouldReturnOkWithList() {
        // Arrange
        List<FavoriteResponse> expectedFavorites = Arrays.asList(favoriteResponse,
                FavoriteResponse.builder().userEmail("another@user.com").movieTitle("Another Movie").build());
        when(favoriteService.getAllFavoritesForAllUsers()).thenReturn(expectedFavorites);

        // Act
        ResponseEntity<List<FavoriteResponse>> responseEntity = favoriteController.getAllFavorites();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(2, responseEntity.getBody().size());
        assertEquals(expectedFavorites, responseEntity.getBody());

        // Verify
        verify(favoriteService, times(1)).getAllFavoritesForAllUsers();
        verifyNoMoreInteractions(favoriteService);
    }

    // --- Test for getFavorite (GET /{userId}/favorites/{favoriteId}) ---
    @Test
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

    // --- Örnek Hata Senaryosu Testi (removeFavorite için) ---
    @Test
    void removeFavorite_whenNotFound_shouldThrowException() {
        // Arrange: Servis çağrıldığında bir exception fırlatmasını sağla
        when(favoriteService.removeFavorite(userId, favoriteId))
                .thenThrow(new ResourceNotFoundException("Favorite not found"));

        // Act & Assert: Belirtilen exception'ın fırlatıldığını doğrula
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            favoriteController.removeFavorite(userId, favoriteId);
        });

        // Exception mesajını kontrol et (opsiyonel ama önerilir)
        assertEquals("Favorite not found", exception.getMessage());

        // Verify: Servis metodunun çağrıldığını doğrula
        verify(favoriteService, times(1)).removeFavorite(userId, favoriteId);
        verifyNoMoreInteractions(favoriteService);
    }
}