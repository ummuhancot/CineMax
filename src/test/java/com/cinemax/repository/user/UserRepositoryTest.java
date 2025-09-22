package com.cinemax.repository.user;

import com.cinemax.entity.concretes.user.User;
import com.cinemax.entity.enums.Gender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    @Test
    void itShouldFindByEmail() {
        // Arrange
        User user = User.builder()
                .id(1L)
                .name("Ahmet")
                .surname("Yılmaz")
                .email("ahmet@example.com")
                .password("Abc123!@#")
                .phoneNumber("(555) 111-2233")
                .birthDate(LocalDate.of(1990, 5, 15))
                .gender(Gender.MALE)
                .build();

        Mockito.when(userRepository.findByEmail("ahmet@example.com"))
                .thenReturn(Optional.of(user));

        // Act
        Optional<User> found = userRepository.findByEmail("ahmet@example.com");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("ahmet@example.com", found.get().getEmail());
    }

    @Test
    void itShouldCheckExistsByEmail() {
        // Arrange
        Mockito.when(userRepository.existsByEmail("mehmet@example.com"))
                .thenReturn(true);

        // Act
        boolean exists = userRepository.existsByEmail("mehmet@example.com");

        // Assert
        assertTrue(exists);
    }

    @Test
    void itShouldCheckExistsByPhoneNumber() {
        // Arrange
        Mockito.when(userRepository.existsByPhoneNumber("(555) 333-4455"))
                .thenReturn(true);

        // Act
        boolean exists = userRepository.existsByPhoneNumber("(555) 333-4455");

        // Assert
        assertTrue(exists);
    }

    @Test
    void itShouldSearchByNameSurnameEmailOrPhoneNumber() {
        // Arrange
        User user = User.builder()
                .id(2L)
                .name("Kemal")
                .surname("Arslan")
                .email("kemal@example.com")
                .phoneNumber("(555) 444-5566")
                .birthDate(LocalDate.of(1995, 12, 1))
                .gender(Gender.MALE)
                .build();

        Page<User> mockPage = new PageImpl<>(List.of(user));

        Mockito.when(userRepository
                        .findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneNumberContainingIgnoreCase(
                                "kemal", "kemal", "kemal@example.com", "444", PageRequest.of(0, 10)))
                .thenReturn(mockPage);

        // Act
        Page<User> result = userRepository
                .findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneNumberContainingIgnoreCase(
                        "kemal", "kemal", "kemal@example.com", "444", PageRequest.of(0, 10));

        // Assert
        assertEquals(1, result.getContent().size());
        assertEquals("kemal@example.com", result.getContent().get(0).getEmail());
    }
}
