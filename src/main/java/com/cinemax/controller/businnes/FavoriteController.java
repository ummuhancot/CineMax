package com.cinemax.controller.businnes;

import com.cinemax.payload.request.business.FavoriteRequest;
import com.cinemax.payload.response.business.FavoriteResponse;
import com.cinemax.service.bussines.FavoriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{userId}")
    public ResponseEntity<FavoriteResponse> addFavoriteMovie(
            @PathVariable Long userId,
            @RequestBody @Valid FavoriteRequest request) {

        FavoriteResponse response = favoriteService.addMovieToFavorites(userId, request);
        return ResponseEntity.status(201).body(response);
    }



    @DeleteMapping("/{userId}/favorites/{favoriteId}")
    public ResponseEntity<FavoriteResponse> removeFavorite(
            @PathVariable Long userId,
            @PathVariable Long favoriteId) {

        FavoriteResponse response = favoriteService.removeFavorite(userId, favoriteId);
        return ResponseEntity.ok(response);
    }

    //bir userın tüm favorilerini getirme
    @GetMapping("/{userId}/getAll")
    public ResponseEntity<List<FavoriteResponse>> getAllFavorites(@PathVariable Long userId) {
        List<FavoriteResponse> favorites = favoriteService.getAllFavorites(userId);
        return ResponseEntity.ok(favorites);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<FavoriteResponse>> getAllFavorites() {
        List<FavoriteResponse> favorites = favoriteService.getAllFavoritesForAllUsers();
        return ResponseEntity.ok(favorites);
    }

    @GetMapping("/{userId}/favorites/{favoriteId}")
    public ResponseEntity<FavoriteResponse> getFavorite(
            @PathVariable Long userId,
            @PathVariable Long favoriteId) {

        FavoriteResponse response = favoriteService.getFavoriteById(userId, favoriteId);
        return ResponseEntity.ok(response);
    }




}
