package com.cinemax.controller.businnes;

import com.cinemax.payload.request.business.ImageRequest;
import com.cinemax.payload.response.business.ImageResponse;
import com.cinemax.service.bussines.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageController {


    private final ImageService imageService;


    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public ResponseEntity<ImageResponse> saveImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "name", required = false) String name
    ) {
        ImageRequest request = ImageRequest.builder()
                .file(file)
                .name(name)
                .build();

        ImageResponse response = imageService.saveImage(request);
        return ResponseEntity.ok(response);
    }
}
