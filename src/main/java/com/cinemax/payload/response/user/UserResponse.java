package com.cinemax.payload.response.user;

import com.cinemax.entity.enums.Gender;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class UserResponse {
    private  Long id;
	private String name;
	private String surname;
	private String email;
	private String phoneNumber;
	private LocalDate birthDate;
	private Gender gender;
}
