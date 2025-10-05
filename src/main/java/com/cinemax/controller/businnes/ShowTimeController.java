package com.cinemax.controller.businnes;

import com.cinemax.payload.response.business.ShowTimeResponse;
import com.cinemax.service.bussines.ShowTimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/show-times")
@RequiredArgsConstructor
public class ShowTimeController {

    private final ShowTimeService showTimeService;

    // T-1: GET /api/show-times/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ShowTimeResponse> getShowTime(@PathVariable Long id) {
        return ResponseEntity.ok(showTimeService.getDetails(id));
    }
}


