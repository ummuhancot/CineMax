package com.cinemax.payload.request.business;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalTime;
@Data
@NoArgsConstructor
@SuperBuilder
public class ShowTimeRequest {
    @NotNull(message = "Date cannot be null")
    @Future(message = "Date must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotNull(message = "Start time cannot be null")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @NotNull(message = "End time cannot be null")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @NotNull(message = "Movie ID cannot be null")
    private Long movieId;

    @NotNull(message = "Hall ID cannot be null")
    private Long hallId;
}
