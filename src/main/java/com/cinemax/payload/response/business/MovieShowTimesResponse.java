package com.cinemax.payload.response.business;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@NoArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MovieShowTimesResponse {

    private Long movieId;
    private String title;
    private List<ShowTimeResponse> showTimes;
}
