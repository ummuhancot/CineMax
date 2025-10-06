package com.cinemax.payload.mappers;

import com.cinemax.entity.concretes.business.Image;
import com.cinemax.payload.request.business.ImageRequest;
import com.cinemax.payload.response.business.ImageMovieResponse;
import com.cinemax.payload.response.business.ImageResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ImageMapper {


    /**
     * Converts an Image entity to a DTO (ImageMovieResponse) for API responses.
     * @param image the Image entity to convert
     * @return ImageMovieResponse DTO
     */


    public ImageResponse mapImageToResponse(Image image) {
        return ImageResponse.builder()
                .id(image.getId())
                .name(image.getName())
                .type(image.getType())
                .build();
    }
    public Image createImageFromRequest(ImageRequest request) {
        MultipartFile file = request.getFile();

        try {
            return Image.builder()
                    .name(request.getName() != null ? request.getName() : file.getOriginalFilename())
                    .type(file.getContentType())
                    .data(file.getBytes())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Error while creating image: " + e.getMessage());
        }
    }

}
