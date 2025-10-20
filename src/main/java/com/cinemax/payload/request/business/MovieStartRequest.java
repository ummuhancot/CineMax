package com.cinemax.payload.request.business;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@NoArgsConstructor
@SuperBuilder
public class MovieStartRequest {

    @NotEmpty(message = "Hall list cannot be empty")
    private List<Long> hallIds;

    @NotNull(message = "DurationDays cannot be null")
    @Min(value = 1, message = "DurationDays must be at least 1")
    private Integer durationDays = 10;

    @NotNull(message = "Special halls list cannot be null")
    private List<String> specialHalls;

}
