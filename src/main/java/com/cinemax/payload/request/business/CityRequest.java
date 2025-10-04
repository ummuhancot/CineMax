package com.cinemax.payload.request.business;

import jakarta.persistence.Column;
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

    @NotBlank(message = "City address cannot be empty")
    @Size(min = 10, message = "City address cannot exceed 30 characters")
    @Size(max = 255, message = "You must enter at least 255 characters for the city address.")
    private String address;

}
