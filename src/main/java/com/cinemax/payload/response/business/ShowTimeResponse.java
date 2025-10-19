package com.cinemax.payload.response.business;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShowTimeResponse {
    private Long id;
    private LocalDate date;
    private LocalTime startDateTime;
    private LocalTime endDateTime;
    private String hallName;
    private Long movieId;
    private String movieTitle;
}
