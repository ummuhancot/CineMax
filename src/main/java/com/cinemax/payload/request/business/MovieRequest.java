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
public class MovieRequest {

    //@NotNull(message = "Movie ID cannot be null") --> notnull diyemeyiz çünkü id otomatik oluşturulacak
    private Long id; //id otomatik oluşacak burada eklememize gerek yok user dan id almıyoruz --TODO - güncellenmeli

    @NotBlank(message = "Title cannot be null")
    @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
    private String title;

    private String slug; // opsiyonel, service’de otomatik üretilebilir

    @NotBlank(message = "Summary cannot be null")
    @Size(min = 10, max = 500, message = "Summary must be between 10 and 500 characters")
    private String summary;

    @NotNull(message = "Release date cannot be null")
    private LocalDate releaseDate;

    @NotNull(message = "Duration cannot be null")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 500, message = "Duration must be less than 500 minutes")
    private Integer duration;

    @DecimalMin(value = "0.0", inclusive = true, message = "Rating must be positive")
    @DecimalMax(value = "10.0", inclusive = true, message = "Rating must be at most 10.0")
    private Double rating; // opsiyonel, entity’de nullable

    //private String specialHalls; // opsiyonel

    @NotBlank(message = "Director cannot be null")
    @Size(min = 2, max = 50, message = "Director name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "Director name must contain only letters and spaces")
    private String director;

    @NotBlank(message = "Genre cannot be null")
    private String genre;

    @NotNull(message = "Poster ID cannot be null")
    private Long posterId; // OneToOne ilişki için, service’de Image entity’ye çevrilecek

    private MovieStatus status = MovieStatus.COMING_SOON;

    @NotNull(message = "Cast list cannot be null")
    private List<String> cast;

    @NotNull(message = "Formats list cannot be null")
    private List<String> formats;

    @NotNull(message = "Hall IDs cannot be null")
    private List<Long> hallIds;

    @NotNull(message = "ShowTimes cannot be null")
    private List<ShowTimeRequest> showTimes;



}
