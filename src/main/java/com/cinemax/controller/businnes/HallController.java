package com.cinemax.controller.businnes;

import com.cinemax.payload.request.business.HallRequest;
import com.cinemax.payload.response.business.HallResponse;
import com.cinemax.service.bussines.HallService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
