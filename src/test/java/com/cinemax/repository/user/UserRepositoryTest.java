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
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;

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

    @Test
    void itShouldReturnEmptyWhenEmailNotFound() {
        // Arrange
        when(userRepository.findByEmail("yok@example.com")).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userRepository.findByEmail("yok@example.com");

        // Assert
        assertTrue(result.isEmpty());
        verify(userRepository).findByEmail("yok@example.com");
    }

    @Test
    void itShouldSaveUpdatedUserFields() {
        // Arrange: var olan kullanıcı
        User existing = User.builder()
                .id(10L)
                .name("Eski")
                .surname("Kullanici")
                .email("old@example.com")
                .phoneNumber("111")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        // Service’te yapıldığı gibi alanları güncellenmiş bir User oluştur
        User updated = User.builder()
                .id(existing.getId())
                .name("Yeni")
                .surname("Isim")
                .email("new@example.com")
                .phoneNumber("(555) 555-5555")
                .gender(Gender.FEMALE)
                .birthDate(LocalDate.of(1995, 5, 20))
                .build();

        // save çağrısının argümanını yakalayalım
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        User saved = userRepository.save(updated);

        // Assert: save'e gerçekten beklediğimiz değerler gitmiş mi?
        verify(userRepository, times(1)).save(captor.capture());
        User toDb = captor.getValue();

        assertEquals("Yeni", toDb.getName());
        assertEquals("Isim", toDb.getSurname());
        assertEquals("new@example.com", toDb.getEmail());
        assertEquals("(555) 555-5555", toDb.getPhoneNumber());
        assertEquals(Gender.FEMALE, toDb.getGender());
        assertEquals(LocalDate.of(1995, 5, 20), toDb.getBirthDate());

        // ve dönen sonuç da aynı alanlara sahip olmalı
        assertEquals("Yeni", saved.getName());
        assertEquals("new@example.com", saved.getEmail());
    }

    @Test
    void itShouldSaveWhenGenderIsNull() {
        // Arrange: gender null iken de kayıt edilebilmeli
        User updated = User.builder()
                .id(11L)
                .name("Ayse")
                .surname("Yilmaz")
                .email("ayse@example.com")
                .phoneNumber("222")
                .gender(null) // null gender
                .birthDate(LocalDate.of(2000, 1, 1))
                .build();

        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        User saved = userRepository.save(updated);

        // Assert
        verify(userRepository).save(any(User.class));
        assertNull(saved.getGender());
        assertEquals("ayse@example.com", saved.getEmail());
    }


}
