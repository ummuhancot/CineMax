package com.cinemax.payload.request.business;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
public class MovieFavoriteRequest {

    @NotNull(message = "E-posta cannot be null")
    private String email;

    @NotNull(message = "Movie ID cannot be null")
    private Long movieId;

}
