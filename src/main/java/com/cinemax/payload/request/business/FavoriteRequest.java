package com.cinemax.payload.request.business;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
public class FavoriteRequest {


    @NotNull(message = "Movie ID cannot be null")
    private Long movieId;

    @NotNull(message = "Cinema ID cannot be null")
    private Long cinemaId;

}
