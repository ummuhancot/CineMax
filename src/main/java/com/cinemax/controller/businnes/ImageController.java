package com.cinemax.controller.businnes;


import com.cinemax.payload.response.business.ImageResponse;
import com.cinemax.service.bussines.ImageService; // Servis importu doğru olmalı
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/images") // Yol PDF ile uyumlu hale getirildi
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService; // Doğru servis enjekte edildiğinden emin olun

    // I02: Bir filme ait yeni resimleri yükler (POST /api/images/{movieId})
    @PostMapping("/{movieId}")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')") // Yetkilendirme PDF'e göre ayarlandı
    public ResponseEntity<List<ImageResponse>> uploadImages(
            @RequestParam("images") List<MultipartFile> images,
            @PathVariable Long movieId
    ) {
        // Servisteki 'createImage' metodunu çağırıyoruz
        List<ImageResponse> response = imageService.createImage(images, movieId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // I01: ID ile resim getirme (GET /api/images/{imageId})
    @GetMapping("/{imageId}")
    // Yetkilendirme PDF'deki gibi public (PreAuthorize yok)
    public ResponseEntity<ImageResponse> getImageById(@PathVariable Long imageId) {
        // Servisteki 'getImageById' metodunu çağırıyoruz (Hata buradaysa servisdeki metot adını kontrol edin)
        ImageResponse response = imageService.getImageById(imageId); // <-- Satır 63 (Tahmini)
        return ResponseEntity.ok(response);
    }

    // I03: ID ile resim silme (DELETE /api/images/{imageId})
    @DeleteMapping("/{imageId}")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')") // Yetkilendirme PDF'e göre ayarlandı
    public ResponseEntity<ImageResponse> deleteImage(@PathVariable Long imageId) {
        // Servisteki 'deleteImageById' metodunu çağırıyoruz (Hata buradaysa servisdeki metot adını kontrol edin)
        ImageResponse deletedImageInfo = imageService.deleteImageById(imageId); // <-- Satır 73 (Tahmini)
        return ResponseEntity.ok(deletedImageInfo);
    }

    // I04: ID ile resim güncelleme - 'featured' durumu (PUT /api/images/{imageId}/featured)
    @PutMapping("/{imageId}/featured")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')") // Yetkilendirme PDF'e göre ayarlandı
    public ResponseEntity<ImageResponse> updateImageFeaturedStatus(
            @PathVariable Long imageId,
            @RequestParam boolean featured) {
        // Servisteki 'updateImageFeaturedStatus' metodunu çağırıyoruz (Hata buradaysa servisdeki metot adını/parametrelerini kontrol edin)
        ImageResponse updatedImage = imageService.updateImageFeaturedStatus(imageId, featured); // <-- Satır 85 (Tahmini)
        return ResponseEntity.ok(updatedImage);
    }

    /* // Yorum satırı olan kod bloğu (Commented out code) - 89. satır civarı
    @PutMapping("/{imageId}")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public ResponseEntity<ImageResponse> replaceImage(
            @PathVariable Long imageId,
            @RequestParam("file") MultipartFile file) {
        // Burası henüz implemente edilmemiş olabilir.
        // ImageResponse updatedImage = imageService.replaceImageFile(imageId, file); // Varsayımsal metot
        // return ResponseEntity.ok(updatedImage);
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
    */

} // Sınıfın kapanış parantezi - Eksik/fazla olmamalı