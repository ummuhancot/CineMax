package com.cinemax.payload.response.business;

import com.cinemax.entity.enums.MovieStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MovieAdminResponse {
    private Long id;
    private String title;
    private String slug;
    private MovieStatus status;
    private LocalDate releaseDate;
}
