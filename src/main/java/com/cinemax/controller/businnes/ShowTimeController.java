package com.cinemax.controller.businnes;
import com.cinemax.payload.request.business.ShowTimeRequest;
import com.cinemax.payload.response.business.ShowTimeResponse;
import com.cinemax.service.bussines.ShowTimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/showtime")
@RequiredArgsConstructor
public class ShowTimeController {

    private final ShowTimeService showTimeService;


    // T-1: GET /api/show-times/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ShowTimeResponse> getShowTime(@PathVariable Long id) {
        return ResponseEntity.ok(showTimeService.getDetails(id));
    }
}



    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('Admin','Manager','Customer')")
    public ResponseEntity<ShowTimeResponse> createShowTime(@Valid @RequestBody ShowTimeRequest request) {
        ShowTimeResponse response = showTimeService.createShowTime(request);
        return ResponseEntity.ok(response);
    }
}

