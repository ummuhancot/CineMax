package com.cinemax.controller.businnes;

import com.cinemax.entity.concretes.business.Cinema;
import com.cinemax.payload.request.business.CinemaRequest;
import com.cinemax.payload.response.business.CinemaHallResponse;
import com.cinemax.payload.response.business.CinemaResponse;
import com.cinemax.service.bussines.CinemaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public List<CinemaHallResponse> getCinemas(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String specialHall
    ) {
        return cinemaService.getCinemas(city, specialHall);
    }

    // POST /api/cinemas
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<CinemaResponse> createCinema(@RequestBody @Valid CinemaRequest request) {
        CinemaResponse response = cinemaService.createCinema(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //Delete cinema yapınca şehir kalıyor bir şehirde birden fazla şube olabilir şube kapatılırsa sadece cinema silinecek
    // DELETE /api/city/auth/{cityId}/cinema/{cinemaId}
    @DeleteMapping("/{cityId}/auth/{cinemaId}")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<Cinema> deleteCinema(
            @PathVariable Long cityId,
            @PathVariable Long cinemaId
    ) {
        Cinema deletedCinema = cinemaService.deleteCinema(cityId, cinemaId);
        return ResponseEntity.ok(deletedCinema);
    }

    //Update cinema
    //email ve phoneNumber de değiştirebilir
    //frontend tarafında güncelleme kısmına bastığında dike eski bilgiler gelir yeni bilgilerle değiştirilir
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<CinemaResponse> updateCinema(
            @PathVariable Long id,
            @RequestBody @Valid CinemaRequest request) {
        CinemaResponse response = cinemaService.updateCinema(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CinemaResponse> getCinemaById(@PathVariable Long id) {
        return ResponseEntity.ok(cinemaService.getCinemaById(id));
    }
}
