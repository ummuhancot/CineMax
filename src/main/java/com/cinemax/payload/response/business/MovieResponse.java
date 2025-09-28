package com.cinemax.payload.response.business;

import com.cinemax.entity.enums.MovieStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MovieResponse {

    private Long id;
    private String title;
    private String slug;
    private String summary;
    private LocalDate releaseDate;
    private Integer duration;
    private Double rating;
    private String specialHalls;
    private String director;
    private String genre;
    private Long posterId;
    private MovieStatus status;
}

