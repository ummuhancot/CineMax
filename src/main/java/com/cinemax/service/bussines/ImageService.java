package com.cinemax.service.bussines;

import com.cinemax.entity.concretes.business.Image;
import com.cinemax.payload.mappers.ImageMapper;
import com.cinemax.payload.request.business.ImageRequest;
import com.cinemax.payload.response.business.ImageResponse;
import com.cinemax.repository.businnes.ImageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;



    @Transactional
    public ImageResponse saveImage(ImageRequest request) {

        Image image = imageMapper.createImageFromRequest(request);
        Image saved = imageRepository.save(image);

        return imageMapper.mapImageToResponse(saved);
    }
}
