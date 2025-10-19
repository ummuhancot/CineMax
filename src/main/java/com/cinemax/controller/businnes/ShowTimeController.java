package com.cinemax.controller.businnes;

import com.cinemax.entity.concretes.business.ShowTime;
import com.cinemax.payload.request.business.ShowTimeRequest;
import com.cinemax.payload.response.business.ShowTimeResponse;
import com.cinemax.service.bussines.ShowTimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/showtime")
@RequiredArgsConstructor
public class ShowTimeController {

    private final ShowTimeService showTimeService;

    /**
     * Yeni ShowTime oluştur
     * Yalnızca yetkili kullanıcılar (Admin, Manager, Customer) erişebilir
     */
    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public ResponseEntity<ShowTimeResponse> createShowTimeForMovie(
            @Valid @RequestBody ShowTimeRequest request) {

        ShowTimeResponse response = showTimeService.createShowTime(request);
        return ResponseEntity.ok(response);
    }

    // Çoklu seans eklemek için yeni method
    @PostMapping("/create-multiple")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public ResponseEntity<List<ShowTimeResponse>> createMultipleShowTimes(
            @Valid @RequestBody List<ShowTimeRequest> requests) {

        List<ShowTimeResponse> responses = showTimeService.createMultipleShowTimes(requests);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShowTimeResponse> getShowTimeById(@PathVariable Long id) {
        ShowTimeResponse response = showTimeService.getShowTimeById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public ResponseEntity<ShowTimeResponse> updateShowTime(
            @PathVariable Long id,
            @Valid @RequestBody ShowTimeRequest request) {

        ShowTimeResponse response = showTimeService.updateShowTime(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public ResponseEntity<ShowTimeResponse> deleteShowTime(@PathVariable Long id) {
        ShowTimeResponse deleted = showTimeService.deleteShowTime(id);
        return ResponseEntity.ok(deleted);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public ResponseEntity<List<ShowTimeResponse>> getAllShowTimes() {
        List<ShowTimeResponse> showTimes = showTimeService.getAllShowTimes();
        return ResponseEntity.ok(showTimes);
    }


}
