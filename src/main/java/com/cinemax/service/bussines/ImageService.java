package com.cinemax.service.bussines;

import com.cinemax.entity.concretes.business.Image;
import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.exception.ImageException;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.mappers.ImageMapper;
import com.cinemax.payload.utils.ImageUtil;
import com.cinemax.payload.utils.MessageUtil;
import com.cinemax.payload.response.business.ImageResponse;
import com.cinemax.repository.businnes.ImageRepository;
import com.cinemax.repository.businnes.MovieRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;
    private final MessageUtil messageUtil;
    private final MovieRepository movieRepository;

    public Image saveImage(MultipartFile request, Long movieId) {

        // Movie'yi veritabanından bul, yoksa hata fırlat
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(messageUtil.getMessage("error.movie.not.found"), movieId)
                ));

        Image image;
        try {
            image = Image.builder()
                    .name(request.getOriginalFilename())       // Resim adı
                    .featured(false)                           // Featured false
                    .type(request.getContentType())            // MIME tipi png/img vs
                    .data(ImageUtil.compressImage(request.getBytes())) // Sıkıştırılmış veri
                    .movie(movie)                              // Movie objesini direkt kullan
                    .build();
        } catch (IOException e) {
            throw new ImageException(e.getMessage());
        }

        return imageRepository.save(image);
    }


    // Çoklu resim oluşturma (Controller tarafından çağrılır)
    @Transactional
    public List<ImageResponse> createImage(List<MultipartFile> files, Long movieId){

        // Eğer gelen dosya listesi boşsa hata fırlat
        if (files == null || files.isEmpty() || files.stream().allMatch(MultipartFile::isEmpty)){
            throw new ImageException(messageUtil.getMessage("error.image.not.found")); // Mesaj anahtarı varsayıldı
        }

        // Filmi bul
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(messageUtil.getMessage("error.movie.not.found"), movieId)));

        List<Image> currentImages = movie.getImages();
        int currentImageCount = (currentImages == null) ? 0 : currentImages.size();

        // Toplam resim sayısı limiti (örn: 10)
        int maxImagesAllowed = 10; // Bu değeri konfigürasyondan almak daha iyi olabilir
        if(currentImageCount + files.size() > maxImagesAllowed){
            throw new ImageException(String.format(messageUtil.getMessage("error.image.too.many"), maxImagesAllowed)); // Mesaj anahtarı varsayıldı
        }

        // Her bir dosyayı kaydet
        List<Image> savedImages = files.stream()
                .filter(file -> !file.isEmpty()) // Boş dosyaları atla
                .map(file -> saveImage(file, movieId))
                .collect(Collectors.toList());

        // Kaydedilen resimleri DTO'ya çevirip döndür
        return savedImages.stream()
                .map(imageMapper::toImageResponse) // ImageMapper'daki metodu kullan
                .collect(Collectors.toList());
    }

    // I01: ID ile resim getirme (Sıkıştırılmış veriyi Base64 olarak döner)

    @Transactional(readOnly = true) // Sadece okuma işlemi
    public ImageResponse getImageById(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(messageUtil.getMessage("error.image.not.found.id"), imageId) // Mesaj anahtarı varsayıldı
                ));

        return imageMapper.toImageResponse(image); // Mapper'daki metodu kullan
    }

    // I03: ID ile resim silme
    @Transactional
    public ImageResponse deleteImageById(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(messageUtil.getMessage("error.image.not.found.id"), imageId) // Mesaj anahtarı varsayıldı
                ));

        // Poster olarak kullanılıyorsa null yap (opsiyonel, iş kuralına bağlı)
        if (image.getPosterOfMovie() != null) {
            Movie movie = image.getPosterOfMovie();
            movie.setPoster(null);
            movieRepository.save(movie); // Movie'yi güncelle
        }

        imageRepository.delete(image);

        // Silinen resmin bilgilerini döndür (Base64 data olmadan)
        return ImageResponse.builder()
                .id(image.getId())
                .name(image.getName())
                .type(image.getType())
                .featured(image.isFeatured())
                .movieId(image.getMovie() != null ? image.getMovie().getId() : null)
                .build();
    }

    // I04: ID ile resim güncelleme (Örn: featured durumunu değiştirme veya dosyayı değiştirme)
    // Not: Dosya değiştirme işlemi genellikle yeni yükleme + eskiyi silme şeklinde yapılır.
    // Burada sadece 'featured' durumunu güncelleyen bir örnek var.
    @Transactional
    public ImageResponse updateImageFeaturedStatus(Long imageId, boolean featured) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(messageUtil.getMessage("error.image.not.found.id"), imageId) // Mesaj anahtarı varsayıldı
                ));

        image.setFeatured(featured);
        Image updatedImage = imageRepository.save(image);

        return imageMapper.toImageResponse(updatedImage);
    }
}

