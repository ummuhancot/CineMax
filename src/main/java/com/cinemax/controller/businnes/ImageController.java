package com.cinemax.controller.businnes;
/**
 ENDPOINTS

 I01 /:imageId
 get - it will get an image of a movie
 /api/images/5

 I02 /:movieId
 post - it will upload images of a movie
 /api/images/23

 I03 /:imageId
 delete - it will the image of a movie
 /api/images/5

 I04 /:imageId
 put - it will update an image of a movie
 /api/images/5

 */
import com.cinemax.payload.response.business.ImageResponse;
import com.cinemax.service.bussines.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class  ImageController {

    private final ImageService imageService;

    /**
     * Bu metod bir filme ait yeni resimleri yükler.
     * @param images Yüklenecek resim dosyaları
     * @param movieId Resimlerin ait olduğu filmin ID'si
     * @return Yüklenen resimlerin bilgilerini döner
     */

    @PostMapping("/{movieId}")
    @PreAuthorize("hasAnyAuthority('Admin')")
    public ResponseEntity<List<ImageResponse>> uploadImage(
            @RequestParam("images") List<MultipartFile> images,
            @PathVariable Long movieId
    ) {
        List<ImageResponse> response = imageService.createImage(images, movieId);
        return ResponseEntity.ok(response);
    }


    // 🔹 GET /api/image/movie/{movieId} → Filmin tüm resimlerini getir
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<ImageResponse>> getImagesByMovie(@PathVariable Long movieId) {
        List<ImageResponse> images = imageService.getImagesByMovieId(movieId);
        return ResponseEntity.ok(images);
    }



}
