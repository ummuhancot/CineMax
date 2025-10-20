package com.cinemax.payload.request.business;

import com.cinemax.entity.enums.MovieStatus;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@SuperBuilder
public class MovieComingSoonRequest {

    @NotBlank(message = "Title cannot be null")
    @Size(min = 2, max = 100)
    private String title;

    @NotBlank(message = "Summary cannot be null")
    @Size(min = 10, max = 500)
    private String summary;

    @NotNull(message = "Release date cannot be null")
    private LocalDate releaseDate;

    @DecimalMin(value = "0.0", inclusive = true, message = "Rating must be positive")
    @DecimalMax(value = "10.0", inclusive = true, message = "Rating must be at most 10.0")
    private Double rating;

    @NotBlank(message = "Director cannot be null")
    @Size(min = 2, max = 50, message = "Director name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z \\-']+$", message = "Director name must contain only letters, spaces, hyphens or apostrophes")
    private String director;

    @NotBlank(message = "Genre cannot be null")
    private String genre;

    @NotNull(message = "Cast list cannot be null")
    private List<String> cast;

    @NotNull(message = "DurationDays cannot be null")
    @Min(value = 1, message = "DurationDays must be at least 1")
    private Integer durationDays = 10;

    @NotNull(message = "Formats list cannot be null")
    private List<String> formats;

    private MovieStatus status = MovieStatus.COMING_SOON;

}

