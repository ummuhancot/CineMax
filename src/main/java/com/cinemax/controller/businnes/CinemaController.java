package com.cinemax.controller.businnes;

import com.cinemax.payload.request.business.CinemaRequest;
import com.cinemax.payload.response.business.CinemaHallResponse;
import com.cinemax.payload.response.business.CinemaResponse;
import com.cinemax.service.bussines.CinemaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cinemas")
@RequiredArgsConstructor
public class CinemaController {

    private final CinemaService cinemaService;

    // GET /api/cinemas/city-hall?city=&specialHall=
    @GetMapping("/city-hall")
    @PreAuthorize("hasAnyAuthority('Admin','Manager','Customer')")
    public ResponseEntity<List<CinemaHallResponse>> getCinemasByCityAndSpecialHall(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String specialHall) {

        List<CinemaHallResponse> cinemas = cinemaService.getCinemas(city, specialHall);
        return ResponseEntity.ok(cinemas);
    }


    // POST /api/cinemas
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<CinemaResponse> createCinema(@RequestBody @Valid CinemaRequest request) {
        CinemaResponse response = cinemaService.createCinema(request);
        return ResponseEntity.status(201).body(response);
    }


}
