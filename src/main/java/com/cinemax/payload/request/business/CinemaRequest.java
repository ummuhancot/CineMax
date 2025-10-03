package com.cinemax.payload.request.business;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
public class CinemaRequest {

    @NotBlank(message = "Cinema name cannot be blank")
    private String name;

    @NotBlank(message = "City name cannot be blank")
    private String cityName;

    @NotBlank(message = "Address cannot be blank")
    private String address;

    @NotBlank(message = "Phone number cannot be blank")
    private String phoneNumber;

    @NotBlank(message = "Email cannot be blank")
    private String email;
}
