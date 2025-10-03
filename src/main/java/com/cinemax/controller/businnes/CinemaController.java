package com.cinemax.controller.businnes;

import com.cinemax.payload.response.business.CinemaResponse;
import com.cinemax.service.bussines.CinemaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cinemas")
@RequiredArgsConstructor
public class CinemaController {

    private final CinemaService cinemaService;

    // GET /api/cinemas/city-hall?city=&specialHall=
    @GetMapping("city-hall")
    @PreAuthorize("hasAnyAuthority('Admin','Manager','Customer')")
    public ResponseEntity<List<CinemaResponse>> getCinemasByCityAndSpecialHall(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String specialHall) {

        List<CinemaResponse> cinemas = cinemaService.getCinemas(city, specialHall);
        return ResponseEntity.ok(cinemas);
    }




}
