package com.cinemax.payload.response.business;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageMovieResponse {

    private Long id;
    private String fileName;
    private String fileType;
    private boolean featured;
    private String data;
    private Long movieId;
    private String movieTitle;
}
