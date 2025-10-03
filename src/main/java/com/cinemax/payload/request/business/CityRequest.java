package com.cinemax.payload.request.business;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
public class CityRequest {

    @NotBlank(message = "City name cannot be empty")
    @Size(max = 30, message = "City name cannot exceed 30 characters")
    private String name;

}
