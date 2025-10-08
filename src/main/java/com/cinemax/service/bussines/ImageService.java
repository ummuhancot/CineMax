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
import jakarta.transaction.Transactional;
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


    @Transactional
    public List<ImageResponse> createImage(List<MultipartFile> request, Long movieId){

        // Eğer gelen istek (request) boşsa, özel bir ImageException fırlatılır.
        if (request.isEmpty()){
            throw new ImageException(String.format(messageUtil.getMessage("error.immage.not.found")));
        }


        // Verilen movieId ile film veritabanında aranır; bulunamazsa ResourceNotFoundException fırlatılır.
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(messageUtil.getMessage("error.movie.not.found"), movieId)));

        List<Image> images = movie.getImages();

        // Eğer mevcut resim sayısı ile yüklenmek istenen resim sayısının toplamı 10'u aşarsa, özel bir ImageException fırlatılır.
        if(images != null && images.size() + request.size() > 10){
            throw new ImageException(String.format(messageUtil.getMessage("error.image.too.many")));
        }

        List<Image> savedImage=request.stream().map(t->saveImage(t, movieId)).toList();
        return savedImage.stream().map(imageMapper::toImageResponse).collect(Collectors.toList());

    }
}
