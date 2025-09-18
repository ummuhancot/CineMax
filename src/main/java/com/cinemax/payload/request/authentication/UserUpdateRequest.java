package com.cinemax.payload.request.authentication;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserUpdateRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String surname;

    @NotBlank
    private String email;

    @Pattern(regexp = "\\(\\d{3}\\) \\d{3}-\\d{4}", message = "Phone must be in format (XXX) XXX-XXXX")
    private String phoneNumber;

    private String gender;

    private LocalDate birthDate;




}
