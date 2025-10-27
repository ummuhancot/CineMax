package com.cinemax.payload.mappers;

import com.cinemax.entity.concretes.business.Image;
import com.cinemax.payload.request.business.ImageRequest;
// ImageMovieResponse importu kaldırıldı, kullanılmıyor gibi.
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
                // Null kontrolü eklendi
                .movieId(savedImage.getMovie() != null ? savedImage.getMovie().getId() : null)

                // Builder ile ImageResponse nesnesini tamamla
                .build();
    }


    private String encodeImage(byte[] imageData) {
        // Base64 encoder kullanarak byte dizisini String formatına çevirir
        // JSON ile frontend'e göndermek veya HTTP üzerinden transfer etmek için uygundur
        // Null kontrolü eklendi
        if (imageData == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(imageData);
    }
    //byte[] --> compress veya decompress resim verisi
    //Base64.getEncoder() --> Java'nin Base64 encoder sınıfı -->
    //encodeToString(imageData) → byte dizisini Base64 string’e çevirir.

    /**
     toImageResponse end
     */

    /**
     * Converts an Image entity to a DTO (ImageResponse without data) for API responses.
     * @param image the Image entity to convert
     * @return ImageResponse DTO without the data field
     */
    public ImageResponse mapImageToResponse(Image image) {
        if (image == null) {
            return null;
        }
        return ImageResponse.builder()
                .id(image.getId())
                .name(image.getName())
                .type(image.getType())
                // featured ve movieId alanları eksikti, eklendi.
                .featured(image.isFeatured())
                .movieId(image.getMovie() != null ? image.getMovie().getId() : null)
                // data alanı burada set edilmiyor
                .build();
    }

    /**
     * YENİ EKLENEN METOT:
     * Image entity'sini ImageResponse DTO'suna dönüştürür (Base64 data HARİÇ).
     * @param image Dönüştürülecek Image entity'si
     * @return Data alanı olmayan ImageResponse DTO
     */
    public ImageResponse toImageResponseWithoutData(Image image) {
        if (image == null) {
            return null;
        }
        // Data alanı olmadan builder ile nesne oluşturuluyor
        return ImageResponse.builder()
                .id(image.getId())
                .name(image.getName())
                .type(image.getType())
                .featured(image.isFeatured())
                .movieId(image.getMovie() != null ? image.getMovie().getId() : null)
                // data alanı burada set edilmiyor
                .build();
    }

    // Bu metot ImageService içinde kullanılmıyor gibi duruyor,
    // ancak varsa diye bırakıldı. ImageRequest içeriği boş olduğu için
    // file değişkeni null olacaktır. Hata verebilir.
    public Image createImageFromRequest(ImageRequest request) {
        // ImageRequest içinde file alanı tanımlı değil, bu nedenle null olacaktır.
        // Bu metot muhtemelen kullanılmamalı veya ImageRequest güncellenmeli.
        MultipartFile file = null; // request.getFile() yerine null atandı.

        // Eğer file null ise işlem yapılamaz.
        if (file == null) {
            throw new IllegalArgumentException("MultipartFile cannot be null in ImageRequest");
        }

        try {
            return Image.builder()
                    .name(request.getName() != null ? request.getName() : file.getOriginalFilename())
                    .type(file.getContentType())
                    .data(ImageUtil.compressImage(file.getBytes())) // compressImage eklendi
                    // movie ilişkisi burada set edilemez, servis katmanında yapılmalı
                    .build();

        } catch (Exception e) {
            // Daha spesifik exception fırlatılabilir.
            throw new RuntimeException("Error while creating image from request: " + e.getMessage(), e);
        }
    }
}