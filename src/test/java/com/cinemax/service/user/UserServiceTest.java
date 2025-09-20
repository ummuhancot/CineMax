package com.cinemax.service.user;

import com.cinemax.entity.concretes.user.User;
import com.cinemax.entity.enums.Gender;
import com.cinemax.exception.ConflictException;
import com.cinemax.payload.mappers.UserMapper;
import com.cinemax.payload.request.user.UserRequest;
import com.cinemax.payload.response.business.ResponseMessage;
import com.cinemax.payload.response.user.UserResponse;
import com.cinemax.repository.user.UserRepository;
import com.cinemax.service.validator.UniquePropertyValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UniquePropertyValidator uniquePropertyValidator;

    @InjectMocks
    private UserService userService;


    @Test
    void saveUser_ShouldReturnResponse_WhenUserDoesNotExist() {
        // Arrange
        UserRequest request = new UserRequest();
        request.setName("Ahmet");
        request.setSurname("Yılmaz");
        request.setEmail("ahmet@example.com");
        request.setPassword("Abc12345@"); // kurala uygun
        request.setPhoneNumber("(555) 111-2233"); // regex'e uygun
        request.setBirthDate(LocalDate.of(1990, 5, 15)); // geçmiş tarih
        request.setGender(Gender.MALE);

        Principal principal = () -> "admin@example.com";

        User userToSave = User.builder()
                .id(1L)
                .name("Ahmet")
                .surname("Yılmaz")
                .email("ahmet@example.com")
                .password("Abc12345@")
                .phoneNumber("(555) 111-2233")
                .birthDate(LocalDate.of(1990, 5, 15))
                .gender(Gender.MALE)
                .build();

        UserResponse userResponse = UserResponse.builder()
                .name("Ahmet")
                .surname("Yılmaz")
                .email("ahmet@example.com")
                .phoneNumber("(555) 111-2233")
                .birthDate(LocalDate.of(1990, 5, 15))
                .gender(Gender.MALE)
                .build();

        // Mocks
        Mockito.when(userRepository.findByEmail("ahmet@example.com"))
                .thenReturn(Optional.empty());
        Mockito.when(userMapper.mapUserRequestToUser(request, "Customer"))
                .thenReturn(userToSave);
        Mockito.when(userRepository.save(any(User.class)))
                .thenReturn(userToSave);
        Mockito.when(userMapper.mapUserToUserResponse(userToSave))
                .thenReturn(userResponse);

        // Act
        ResponseMessage<UserResponse> response =
                userService.saveUser(request, "Customer", principal);

        // Assert
        assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getReturnBody().getEmail()).isEqualTo("ahmet@example.com");
        assertThat(response.getReturnBody().getName()).isEqualTo("Ahmet");
        assertThat(response.getReturnBody().getPhoneNumber()).isEqualTo("(555) 111-2233");
        assertThat(response.getReturnBody().getBirthDate()).isEqualTo(LocalDate.of(1990, 5, 15));
        assertThat(response.getReturnBody().getGender()).isEqualTo(Gender.MALE);

    }

    @Test
    void saveUser_ShouldThrowConflictException_WhenUserAlreadyExists() {
        // Arrange
        UserRequest request = new UserRequest();
        request.setEmail("ahmet@example.com");

        Principal principal = () -> "admin@example.com";

        User existingUser = User.builder()
                .id(1L)
                .email("ahmet@example.com")
                .build();

        // Mocks
        Mockito.when(userRepository.findByEmail("ahmet@example.com"))
                .thenReturn(Optional.of(existingUser));

        // Act & Assert
        assertThatThrownBy(() ->
                userService.saveUser(request, "Customer", principal)
        ).isInstanceOf(ConflictException.class)
                .hasMessageContaining("User already exists");
    }
}

