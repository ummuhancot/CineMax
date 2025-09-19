package com.cinemax.service.user;

import com.cinemax.entity.concretes.user.User;
import com.cinemax.entity.enums.Gender;
import com.cinemax.exception.ConflictException;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.mappers.UserMapper;
import com.cinemax.payload.request.authentication.UserUpdateRequest;
import com.cinemax.payload.response.user.UserResponse;
import com.cinemax.repository.user.UserRepository;
import com.cinemax.security.jwt.JwtUtils;
import com.cinemax.service.validator.UniquePropertyValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    private AuthenticationService authenticationService;

    private JwtUtils jwtUtils;
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private UniquePropertyValidator uniquePropertyValidator;
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        jwtUtils = mock(JwtUtils.class);
        authenticationManager = mock(AuthenticationManager.class);
        userRepository = mock(UserRepository.class);
        uniquePropertyValidator = mock(UniquePropertyValidator.class);
        userMapper = mock(UserMapper.class);

        authenticationService = new AuthenticationService(
                jwtUtils,
                authenticationManager,
                userRepository,
                uniquePropertyValidator,
                userMapper
        );
    }

    @Test
    void shouldUpdateAuthenticatedUserSuccessfully() {
        String email = "user@example.com";
        Principal principal = () -> email;

        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setBuiltIn(false);

        UserUpdateRequest request = new UserUpdateRequest();
        request.setName("Ali");
        request.setSurname("Veli");
        request.setEmail(email);
        request.setPhoneNumber("(555) 555-5555");
        request.setGender("MALE");
        request.setBirthDate(LocalDate.of(1995, 5, 20));

        UserResponse expectedResponse = UserResponse.builder()
                .id(1L) // ✅ Bu satır artık hata vermez çünkü UserResponse'da id var
                .name("Ali")
                .surname("Veli")
                .email(email)
                .phoneNumber("(555) 555-5555")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1995, 5, 20))
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.mapUserToUserResponse(any(User.class))).thenReturn(expectedResponse);

        UserResponse actualResponse = authenticationService.updateAuthenticateduser(request, principal);

        assertEquals("Ali", actualResponse.getName());
        assertEquals("Veli", actualResponse.getSurname());
        assertEquals(Gender.MALE, actualResponse.getGender());
        assertEquals("(555) 555-5555", actualResponse.getPhoneNumber());

        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).mapUserToUserResponse(user);
    }

    @Test
    void shouldThrowConflictExceptionForBuiltInUser() {
        String email = "builtin@example.com";
        Principal principal = () -> email;

        User user = new User();
        user.setBuiltIn(true);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserUpdateRequest request = new UserUpdateRequest();

        assertThrows(ConflictException.class, () ->
                authenticationService.updateAuthenticateduser(request, principal));
    }

    @Test
    void shouldThrowResourceNotFoundExceptionIfUserNotFound() {
        String email = "notfound@example.com";
        Principal principal = () -> email;

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        UserUpdateRequest request = new UserUpdateRequest();

        assertThrows(ResourceNotFoundException.class, () ->
                authenticationService.updateAuthenticateduser(request, principal));
    }
}
