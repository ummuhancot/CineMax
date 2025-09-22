package com.cinemax.service.user;

import com.cinemax.entity.concretes.user.User;
import com.cinemax.entity.enums.Gender;
import com.cinemax.exception.ConflictException;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.mappers.UserMapper;
import com.cinemax.payload.messages.SuccessMessages;
import com.cinemax.payload.request.user.UserRequest;
import com.cinemax.payload.response.abstracts.BaseUserResponse;
import com.cinemax.payload.response.business.ResponseMessage;
import com.cinemax.payload.response.user.UserResponse;
import com.cinemax.repository.user.UserRepository;
import com.cinemax.service.helper.MethodHelper;
import com.cinemax.service.helper.PageableHelper;
import com.cinemax.service.validator.UniquePropertyValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UniquePropertyValidator uniquePropertyValidator;

    @Mock
    private MethodHelper methodHelper;

    @InjectMocks
    private UserService userService;

    @Mock
    private PageableHelper pageableHelper;

    private Principal principal;
    private User user;

    @BeforeEach
    void setUp() {
        principal = () -> "admin@test.com";

        user = new User();
        user.setId(1L);
        user.setName("Ali");
        user.setSurname("Veli");
        user.setEmail("admin@test.com");
        user.setPhoneNumber("12345");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setGender(Gender.MALE);
    }

    // ✅ saveUser başarılı
    @Test
    void saveUser_ShouldReturnResponse_WhenUserDoesNotExist() {
        UserRequest request = new UserRequest();
        request.setName("Ahmet");
        request.setSurname("Yılmaz");
        request.setEmail("ahmet@test.com");
        request.setPhoneNumber("5551112233");
        request.setBirthDate(LocalDate.of(1990, 5, 15));
        request.setGender(Gender.MALE);

        User userToSave = new User();
        userToSave.setId(2L);
        userToSave.setName("Ahmet");
        userToSave.setSurname("Yılmaz");
        userToSave.setEmail("ahmet@test.com");

        UserResponse userResponse = new UserResponse();
        userResponse.setName("Ahmet");
        userResponse.setSurname("Yılmaz");
        userResponse.setEmail("ahmet@test.com");

        when(userRepository.findByEmail("ahmet@test.com")).thenReturn(Optional.empty());
        doNothing().when(uniquePropertyValidator).checkDuplication(anyString(), anyString());
        when(userMapper.mapUserRequestToUser(request, "Customer")).thenReturn(userToSave);
        when(userRepository.save(any(User.class))).thenReturn(userToSave);
        when(userMapper.mapUserToUserResponse(userToSave)).thenReturn(userResponse);

        ResponseMessage<UserResponse> response = userService.saveUser(request, "Customer", principal);

        assertEquals(HttpStatus.CREATED, response.getHttpStatus());
        assertEquals("Ahmet", response.getReturnBody().getName());
        assertEquals("ahmet@test.com", response.getReturnBody().getEmail());
    }

    // ❌ saveUser e-posta zaten var
    @Test
    void saveUser_ShouldThrowConflictException_WhenUserAlreadyExists() {
        UserRequest request = new UserRequest();
        request.setEmail("admin@test.com");

        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(user));

        assertThrows(ConflictException.class,
                () -> userService.saveUser(request, "Customer", principal));

        verify(userRepository).findByEmail("admin@test.com");
        verify(userRepository, never()).save(any());
    }

    // ✅ getAllUsersWithQuery
    @Test
    void getAllUsersWithQuery_ShouldReturnPageOfUserResponse() {
        // Arrange
        PageRequest mockPageRequest = PageRequest.of(0, 10, Sort.by("id").ascending());
        when(pageableHelper.getPageable(0, 10, "id", "asc")).thenReturn(mockPageRequest);

        User user = new User();
        user.setId(1L);
        user.setName("Ali");

        Page<User> users = new PageImpl<>(List.of(user));
        when(userRepository.findAll(mockPageRequest)).thenReturn(users);

        UserResponse userResponse = UserResponse.builder().name("Ali").build();
        when(userMapper.mapUserToUserResponse(user)).thenReturn(userResponse);

        // Act
        Page<UserResponse> result = userService.getAllUsersWithQuery("", 0, 10, "id", "asc");

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("Ali", result.getContent().get(0).getName());
    }


    // ✅ findUserById başarılı
    @Test
    void findUserById_ShouldReturnResponse_WhenUserExists() {
        User targetUser = new User();
        targetUser.setId(2L);
        targetUser.setName("Ayşe");
        targetUser.setEmail("ayse@test.com");

        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(user));
        when(methodHelper.isUserExist(2L)).thenReturn(targetUser);

        UserResponse mappedResponse = new UserResponse();
        mappedResponse.setName("Ayşe");
        mappedResponse.setEmail("ayse@test.com");

        when(userMapper.mapUserToUserResponse(targetUser)).thenReturn(mappedResponse);

        ResponseMessage<BaseUserResponse> response = userService.findUserById(2L, principal);

        assertEquals(SuccessMessages.USER_FETCHED_SUCCESS, response.getMessage());
        assertEquals("Ayşe", response.getReturnBody().getName());
        assertEquals("ayse@test.com", response.getReturnBody().getEmail());
    }

    // ❌ findUserById kendini çekmeye çalışırsa
    @Test
    void findUserById_ShouldThrowConflict_WhenFetchingOwnUser() {
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(user));

        assertThrows(ConflictException.class,
                () -> userService.findUserById(1L, principal));
    }

    // ❌ findUserById email yoksa
    @Test
    void findUserById_ShouldThrowNotFound_WhenEmailNotExist() {
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.findUserById(2L, principal));
    }
}
