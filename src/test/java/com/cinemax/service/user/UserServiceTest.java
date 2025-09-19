package com.cinemax.service.user;

import com.cinemax.entity.concretes.user.User;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.mappers.UserMapper;
import com.cinemax.payload.response.user.UserResponse;
import com.cinemax.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test");
        user.setEmail("test@mail.com");

        userResponse = UserResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    @Test
    void getUserById_ShouldReturnUserResponse_WhenUserExists() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userMapper.mapUserToUserResponse(user)).thenReturn(userResponse);

        UserResponse response = userService.getUserById(user.getId());

        assertEquals(userResponse.getName(), response.getName());
        assertEquals(userResponse.getEmail(), response.getEmail());
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserDoesNotExist() {
        Long nonExistentId = 999L;
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(nonExistentId));
    }
}
