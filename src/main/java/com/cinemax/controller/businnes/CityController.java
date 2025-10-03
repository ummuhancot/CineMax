package com.cinemax.controller.businnes;

import com.cinemax.entity.concretes.business.City;
import com.cinemax.payload.request.business.CityRequest;
import com.cinemax.payload.response.business.CityResponse;
import com.cinemax.payload.response.business.CityWithCinemasResponse;
import com.cinemax.service.bussines.CityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cities")
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<CityResponse> saveCity(@Valid @RequestBody CityRequest request) {
        CityResponse response = cityService.saveCity(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/city-cinema/{id}")
    @PreAuthorize("hasAuthority('Admin','Manager','Customer')")
    public ResponseEntity<CityWithCinemasResponse> getCityWithCinemas(@PathVariable Long id) {
        CityWithCinemasResponse response = cityService.getCityWithCinemas(id);
        return ResponseEntity.ok(response);
    }

    //Delete city yapınca sinema da siliniyor yapınca
    // DELETE /api/city/auth/{cityId}
    @DeleteMapping("/{cityId}")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<City> deleteCity(@PathVariable Long cityId) {
        City deletedCity = cityService.deleteCity(cityId);
        return ResponseEntity.ok(deletedCity);
    }

}
