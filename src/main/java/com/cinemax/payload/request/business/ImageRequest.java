package com.cinemax.payload.request.business;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@SuperBuilder
public class ImageRequest {
    private String name;
    private MultipartFile file;
}
