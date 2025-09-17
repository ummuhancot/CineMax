package com.cinemax.payload.request.authentication;

import com.cinemax.entity.enums.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

	@NotBlank(message = "Name cannot be null")
	@Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
	@Pattern(regexp = "^[a-zA-Z]+$", message = "Name must contain only letters")
	private String name;

	@NotBlank(message = "Surname cannot be null")
	@Size(min = 2, max = 50, message = "Surname must be between 2 and 50 characters")
	@Pattern(regexp = "^[a-zA-Z]+$", message = "Surname must contain only letters")
	private String surname;

	@NotBlank(message = "Password cannot be null")
	@Size(min = 8, max = 30, message = "Password must be at least 8 and at most 30 characters long")
	@Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", message = "Password must contain at least one digit, one lowercase, one uppercase letter and one special character")
	private String password;

	@NotBlank(message = "Email cannot be null")
	@Size(min = 5, max = 50, message = "Email must be between 5 and 50 characters")
	@Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Email should be valid")
	private String email;

	@NotBlank(message = "Phone number cannot be null")
	@Size(min = 8, max = 20, message = "Phone number must be between 8 and 20 characters")
	@Pattern(regexp = "^\\(\\d{3}\\) \\d{3}-\\d{4}$", message = "Phone Number should be in form of (XXX) XXX-XXXX")
	private String phoneNumber;

	@NotNull(message = "Birth date cannot be null")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Past(message = "Birth date must be in the past")
	private LocalDate birthDate;

	@NotNull(message = "Gender cannot be null")
	private Gender gender;
}
