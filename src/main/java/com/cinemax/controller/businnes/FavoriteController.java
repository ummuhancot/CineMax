package com.cinemax.controller.businnes;

import com.cinemax.payload.request.business.MovieFavoriteRequest;
import com.cinemax.payload.response.business.FavoriteMovieResponse;
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

    @PostMapping("/movie-created")
    public ResponseEntity<String> addFavoriteMovie(
            @RequestBody @Valid MovieFavoriteRequest request) {

        favoriteService.addMovieToFavorites(request);
        return ResponseEntity.ok("Movie added to favorites successfully!");
    }


    @DeleteMapping("/movie-delete")
    public ResponseEntity<String> removeFavoriteMovie(
            @RequestBody @Valid MovieFavoriteRequest request) {

        String message = favoriteService.removeMovieFromFavorites(request);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/getAll/{email}")
    public ResponseEntity<List<FavoriteMovieResponse>> getAllFavorites(@PathVariable String email) {
        List<FavoriteMovieResponse> favorites = favoriteService.getAllFavorites(email);
        return ResponseEntity.ok(favorites);
    }



}
