package com.cinemax.controller.businnes;

import com.cinemax.payload.request.business.HallRequest;
import com.cinemax.payload.response.business.HallResponse;
import com.cinemax.service.bussines.HallService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/halls")
@RequiredArgsConstructor
public class HallController {

    private final HallService hallService;

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('Admin')")
    public ResponseEntity<HallResponse> saveHall(@RequestBody @Valid HallRequest request) {
        HallResponse response = hallService.saveHall(request);
        return ResponseEntity.status(201).body(response);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public ResponseEntity<HallResponse> getHallById(@PathVariable Long id) {
        HallResponse hallResponse = hallService.getHallById(id);
        return ResponseEntity.ok(hallResponse);
    }

    @DeleteMapping("/deleted/{id}")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public ResponseEntity<HallResponse> deleteHall(@PathVariable Long id) {
        HallResponse deletedHall = hallService.deleteHall(id);
        return ResponseEntity.ok(deletedHall);
    }

    @GetMapping("/getAllHall")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public ResponseEntity<List<HallResponse>> getAllHalls() {
        List<HallResponse> halls = hallService.getAllHalls();
        return ResponseEntity.ok(halls);
    }

    @PutMapping("/updateHall/{id}")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public ResponseEntity<HallResponse> updateHall(
            @PathVariable Long id,
            @Valid @RequestBody HallRequest request) {
        HallResponse updatedHall = hallService.updateHall(id, request);
        return ResponseEntity.ok(updatedHall);
    }




}
