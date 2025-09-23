package com.cinemax.controller.user;

import com.cinemax.entity.enums.Gender;
import com.cinemax.payload.request.authentication.LoginRequest;
import com.cinemax.payload.request.authentication.RegisterRequest;
import com.cinemax.payload.response.authentication.AuthenticationResponse;
import com.cinemax.payload.response.user.UserResponse;
import com.cinemax.service.user.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerTest {

	@Mock
	AuthenticationService authenticationService;

	@InjectMocks
	AuthenticationController authenticationController;

	@Test
	void authenticate_ShouldReturnResponseEntityWithAuthenticationResponse_WhenLoginRequestIsValid() {
		LoginRequest loginRequest = new LoginRequest("deneme@mail.com", "Abc123456**");
		AuthenticationResponse expectedResponse = new AuthenticationResponse("token");

		when(authenticationService.authenticate(loginRequest)).thenReturn(expectedResponse);

		ResponseEntity<AuthenticationResponse> response = authenticationController.authenticate(loginRequest);

		assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
		assertEquals(expectedResponse, response.getBody());
	}


	@Test
	void authenticate_ShouldThrowException_WhenLoginRequestIsInvalid() {
		LoginRequest loginRequest = new LoginRequest(null, null);
		when(authenticationService.authenticate(loginRequest)).thenThrow(new RuntimeException("Invalid credentials"));

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			authenticationController.authenticate(loginRequest);
		});

		assertEquals("Invalid credentials", exception.getMessage());

	}

	@Test
	void register_ShouldReturnResponseEntityWithUserResponse_WhenRegisterRequestIsValid() {
		RegisterRequest registerRequest = new RegisterRequest("name", "surname", "password", "email", "phoneNumber", LocalDate.of(1995, 11, 11), Gender.MALE);
		UserResponse expectedResponse = UserResponse.builder()
					                                .name("name")
					                                .surname("surname")
					                                .email("email")
					                                .phoneNumber("phoneNumber")
					                                .birthDate(LocalDate.of(1995, 11, 11))
					                                .gender(Gender.MALE)
					                                .build();

		when(authenticationService.register(registerRequest)).thenReturn(expectedResponse);

		ResponseEntity<UserResponse> response = authenticationController.register(registerRequest);

		assertEquals(HttpStatusCode.valueOf(201), response.getStatusCode());
		assertEquals(expectedResponse, response.getBody());
	}

	@Test
	void register_ShouldThrowException_WhenRegisterRequestIsInvalid() {
		RegisterRequest registerRequest = new RegisterRequest(null, null, null, null, null, null, null);
		when(authenticationService.register(registerRequest)).thenThrow(new RuntimeException("Registration failed"));

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			authenticationController.register(registerRequest);
		});

		assertEquals("Registration failed", exception.getMessage());

	}

}
