package com.cinemax.payload.mappers;

import com.cinemax.entity.concretes.business.Image;
import com.cinemax.payload.request.business.ImageRequest;
import com.cinemax.payload.response.business.ImageMovieResponse;
import com.cinemax.payload.response.business.ImageResponse;
import com.cinemax.payload.utils.ImageUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

@Component
public class ImageMapper {

    /**
     toImageResponse start
     */
    public ImageResponse toImageResponse(Image savedImage) {
        // Image entity'sini API üzerinde dönecek ImageResponse DTO'suna dönüştürür
        return ImageResponse.builder()

                // ID alanı: kaydedilmiş Image nesnesinin benzersiz ID'si
                .id(savedImage.getId())

                // Name alanı: resmin dosya adı
                .name(savedImage.getName())

                // Type alanı: resmin MIME tipi (örn. image/png, image/jpeg)
                .type(savedImage.getType())

                // Featured alanı: resmin öne çıkan (featured) olup olmadığını belirler
                .featured(savedImage.isFeatured())

                // Data alanı: veritabanında sıkıştırılmış resmi açar (decompress)
                // ve Base64 formatına çevirir, JSON üzerinden frontend'e gönderilebilir
                .data(encodeImage(
                        ImageUtil.decompressImage(savedImage.getData())
                ))

                // MovieId alanı: resmin hangi filme ait olduğunu belirtir
                .movieId(savedImage.getMovie().getId())

                // Builder ile ImageResponse nesnesini tamamla
                .build();
    }


    public String encodeImage(byte[] imageData) {
        // Base64 encoder kullanarak byte dizisini String formatına çevirir
        // JSON ile frontend'e göndermek veya HTTP üzerinden transfer etmek için uygundur
        return Base64.getEncoder().encodeToString(imageData);
    }
    //byte[] --> compress veya decompress resim verisi
    //Base64.getEncoder() --> Java'nin Base64 encoder sınıfı -->
    //encodeToString(imageData) → byte dizisini Base64 string’e çevirir.

    /**
     toImageResponse end
     */

    public ImageMovieResponse toImageMovieResponse(Image image) {
        return ImageMovieResponse.builder()
                .id(image.getId())
                .fileName(image.getName())
                .fileType(image.getType())
                .featured(image.isFeatured())
                .data(encodeImage(
                        ImageUtil.decompressImage(image.getData())
                ))
                .movieId(image.getMovie() != null ? image.getMovie().getId() : null)
                .movieTitle(image.getMovie() != null ? image.getMovie().getTitle() : null)
                .build();
    }
}
