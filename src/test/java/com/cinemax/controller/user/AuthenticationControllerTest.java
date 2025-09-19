package com.cinemax.controller.user;

import com.cinemax.entity.enums.Gender;
import com.cinemax.payload.request.authentication.LoginRequest;
import com.cinemax.payload.request.authentication.RegisterRequest;
import com.cinemax.payload.request.authentication.UserUpdateRequest;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RestController
@RequestMapping("/api/user")
public class AuthenticationControllerTest {

    @Mock
    AuthenticationService authenticationService;

    @InjectMocks
    AuthenticationController authenticationController;

    @Test
    void authenticate_ShouldReturnResponseEntityWithAuthenticationResponse_WhenLoginRequestIsValid() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("deneme@mail.com", "Abc123456**");
        AuthenticationResponse expectedResponse = new AuthenticationResponse("token");

        when(authenticationService.authenticate(loginRequest)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<AuthenticationResponse> response = authenticationController.authenticate(loginRequest);

        // Assert
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void authenticate_ShouldThrowException_WhenLoginRequestIsInvalid() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest(null, null);
        when(authenticationService.authenticate(loginRequest)).thenThrow(new RuntimeException("Invalid credentials"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authenticationController.authenticate(loginRequest);
        });

        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    void register_ShouldReturnResponseEntityWithUserResponse_WhenRegisterRequestIsValid() {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest(
                "name", "surname", "password", "email", "phoneNumber",
                LocalDate.of(1995, 11, 11), Gender.MALE);

        UserResponse expectedResponse = UserResponse.builder()
                .name("name")
                .surname("surname")
                .email("email")
                .phoneNumber("phoneNumber")
                .birthDate(LocalDate.of(1995, 11, 11))
                .gender(Gender.MALE)
                .build();

        when(authenticationService.register(registerRequest)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<UserResponse> response = authenticationController.register(registerRequest);

        // Assert
        assertEquals(HttpStatusCode.valueOf(201), response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void register_ShouldThrowException_WhenRegisterRequestIsInvalid() {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest(null, null, null, null, null, null, null);
        when(authenticationService.register(registerRequest)).thenThrow(new RuntimeException("Registration failed"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authenticationController.register(registerRequest);
        });

        assertEquals("Registration failed", exception.getMessage());
    }

    @Test
    void updateAuthenticatedUser_ShouldReturnUpdatedUserResponse_WhenRequestIsValid() {
        // Arrange
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setName("UpdatedName");
        updateRequest.setSurname("UpdatedSurname");
        updateRequest.setEmail("updated@example.com");
        updateRequest.setPhoneNumber("(555) 123-4567");
        updateRequest.setGender("MALE");
        updateRequest.setBirthDate(LocalDate.of(1990, 1, 1));

        Principal mockPrincipal = () -> "updated@example.com";

        UserResponse expectedResponse = UserResponse.builder()
                .id(1L)
                .name("UpdatedName")
                .surname("UpdatedSurname")
                .email("updated@example.com")
                .phoneNumber("(555) 123-4567")
                .birthDate(LocalDate.of(1990, 1, 1))
                .gender(Gender.MALE)
                .build();

        when(authenticationService.updateAuthenticateduser(updateRequest, mockPrincipal))
                .thenReturn(expectedResponse);

        // Act
        ResponseEntity<UserResponse> response = authenticationController.updateAuthenticatedUser(updateRequest, mockPrincipal);

        // Assert
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }
    @PutMapping("/auth")
    public ResponseEntity<UserResponse> updateAuthenticatedUser(
            @RequestBody UserUpdateRequest request,
            Principal principal) {

        UserResponse updatedUser = authenticationService.updateAuthenticateduser(request, principal);
        return ResponseEntity.ok(updatedUser);
    }

}
