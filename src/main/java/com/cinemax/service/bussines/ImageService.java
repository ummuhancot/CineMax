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
// Doğru Transactional import edildiğinden emin olun
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

    // Tek resim kaydetme (dahili kullanım)
    // Not: Bu metodun @Transactional olması gerekebilir, çağıran metodlar @Transactional olduğu için şimdilik eklenmedi.
    private Image saveImage(MultipartFile file, Long movieId) { // 'request' -> 'file' olarak değiştirildi daha anlaşılır olması için

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(messageUtil.getMessage("error.movie.not.found"), movieId)
                ));

        Image image;
        try {
            image = Image.builder()
                    .name(file.getOriginalFilename())
                    .featured(false)
                    .type(file.getContentType())
                    .data(ImageUtil.compressImage(file.getBytes()))
                    .movie(movie)
                    .build();
        } catch (IOException e) {
            // IOException yerine ImageException fırlatmak daha uygun olabilir
            throw new ImageException("Could not process image file: " + file.getOriginalFilename(), e);
        }

        return imageRepository.save(image);
    }

    // Çoklu resim oluşturma
    @Transactional // Yazma işlemi olduğu için
    public List<ImageResponse> createImage(List<MultipartFile> files, Long movieId) { // 'request' -> 'files' olarak değiştirildi

        // Gelen listenin null veya boş olup olmadığını kontrol et
        if (files == null || files.isEmpty() || files.stream().allMatch(MultipartFile::isEmpty)) {
            // Mesaj anahtarı varsayıldı, kendi anahtarınızı kullanın
            throw new ImageException(messageUtil.getMessage("error.image.list.empty"));
        }

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(messageUtil.getMessage("error.movie.not.found"), movieId)));

        List<Image> currentImages = movie.getImages();
        int currentImageCount = (currentImages == null) ? 0 : currentImages.size();

        int maxImagesAllowed = 10; // Limiti bir konfigürasyon dosyasından almak daha iyi
        // Boş olmayan dosya sayısını hesapla
        long validFilesCount = files.stream().filter(file -> !file.isEmpty()).count();

        if (currentImageCount + validFilesCount > maxImagesAllowed) {
            // Mesaj anahtarı varsayıldı
            throw new ImageException(String.format(messageUtil.getMessage("error.image.too.many"), maxImagesAllowed));
        }

        // Boş olmayan dosyaları işle ve kaydet
        List<Image> savedImages = files.stream()
                .filter(file -> !file.isEmpty())
                .map(file -> saveImage(file, movieId)) // saveImage metodu çağrılıyor
                .collect(Collectors.toList());

        // Kaydedilenleri DTO'ya çevir
        return savedImages.stream()
                .map(imageMapper::toImageResponse) // ImageMapper'daki metot kullanılıyor
                .collect(Collectors.toList());
    }

    // I01: ID ile resim getirme
    @Transactional(readOnly = true) // Sadece okuma işlemi
    public ImageResponse getImageById(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        // Mesaj anahtarı varsayıldı
                        String.format(messageUtil.getMessage("error.image.not.found.id"), imageId)
                ));
        // Mapper ile DTO'ya çevir ve döndür
        return imageMapper.toImageResponse(image);
    }

    // I03: ID ile resim silme
    @Transactional // Veritabanını değiştirdiği için
    public ImageResponse deleteImageById(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        // Mesaj anahtarı varsayıldı
                        String.format(messageUtil.getMessage("error.image.not.found.id"), imageId)
                ));

        // Eğer resim bir filmin posteri ise, filmden poster bilgisini kaldır
        if (image.getPosterOfMovie() != null) {
            Movie movie = image.getPosterOfMovie();
            movie.setPoster(null);
            movieRepository.save(movie); // Film entity'sini güncelle
        }

        imageRepository.delete(image); // Resmi sil

        // Silinen resmin bilgilerini (data olmadan) döndür
        return imageMapper.toImageResponseWithoutData(image); // Data'sız mapper metodu kullanılırsa daha iyi
        /* Eğer toImageResponseWithoutData metodu yoksa:
        return ImageResponse.builder()
               .id(image.getId())
               .name(image.getName())
               .type(image.getType())
               .featured(image.isFeatured())
               .movieId(image.getMovie() != null ? image.getMovie().getId() : null)
               .build();
        */
    }

    // I04: ID ile resmin 'featured' durumunu güncelleme
    @Transactional // Veritabanını değiştirdiği için
    public ImageResponse updateImageFeaturedStatus(Long imageId, boolean featured) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        // Mesaj anahtarı varsayıldı
                        String.format(messageUtil.getMessage("error.image.not.found.id"), imageId)
                ));

        image.setFeatured(featured); // featured durumunu güncelle
        Image updatedImage = imageRepository.save(image); // Değişikliği kaydet

        // Güncellenmiş resmi DTO olarak döndür
        return imageMapper.toImageResponse(updatedImage);
    }
}