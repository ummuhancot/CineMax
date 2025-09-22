package com.cinemax.payload.response.abstracts;

import com.cinemax.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class BaseUserResponse {

    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private LocalDate birthDate;
    private Gender gender;

}
