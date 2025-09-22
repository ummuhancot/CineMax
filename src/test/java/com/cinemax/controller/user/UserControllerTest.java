package com.cinemax.controller.user;

import com.cinemax.entity.enums.Gender;
import com.cinemax.payload.request.authentication.UserUpdateRequest;
import com.cinemax.payload.request.user.UserRequest;
import com.cinemax.payload.response.abstracts.BaseUserResponse;
import com.cinemax.payload.response.business.ResponseMessage;
import com.cinemax.payload.response.user.UserResponse;
import com.cinemax.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

	@Mock
	UserService userService;

	@InjectMocks
	UserController userController;

	@Test
	void getAllUsersWithQuery_ShouldReturnResponseEntityWithPageOfUserResponse_WhenParametersAreValid() {
		String q = "test";
		int page = 0;
		int size = 20;
		String sort = "id";
		String type = "desc";

		@SuppressWarnings("unchecked")
		Page<UserResponse> userResponsePage = Mockito.mock(Page.class);

		when(userService.getAllUsersWithQuery(q, page, size, sort, type)).thenReturn(userResponsePage);

		ResponseEntity<Page<UserResponse>> response = userController.getAllUsersWithQuery(q, page, size, sort, type);

		assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
		assertEquals(userResponsePage, response.getBody());


	}

    // ✅ POST saveUser - başarılı
    @Test
    void saveUser_ShouldReturn201Created_WhenRequestIsValid() {
        UserRequest request = UserRequest.builder()
                .name("Ahmet")
                .surname("Yılmaz")
                .email("ahmet@example.com")
                .password("Abc123!@#")
                .phoneNumber("(555) 111-2233")
                .birthDate(LocalDate.of(1990, 5, 15))
                .gender(Gender.MALE)
                .build();

        UserResponse responseBody = UserResponse.builder()
                .name("Ahmet")
                .surname("Yılmaz")
                .email("ahmet@example.com")
                .phoneNumber("(555) 111-2233")
                .birthDate(LocalDate.of(1990, 5, 15))
                .gender(Gender.MALE)
                .build();

        ResponseMessage<UserResponse> responseMessage = ResponseMessage.<UserResponse>builder()
                .message("User created successfully")
                .httpStatus(HttpStatus.CREATED)
                .returnBody(responseBody)
                .build();

        Principal principal = () -> "admin";

        when(userService.saveUser(any(UserRequest.class), eq("Customer"), any(Principal.class)))
                .thenReturn(responseMessage);

        ResponseEntity<ResponseMessage<UserResponse>> response =
                userController.saveUser(request, "Customer", principal);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User created successfully", response.getBody().getMessage());
        assertEquals("ahmet@example.com", response.getBody().getReturnBody().getEmail());
        assertEquals("Ahmet", response.getBody().getReturnBody().getName());
    }

    // ❌ POST saveUser - BAD_REQUEST
    @Test
    void saveUser_ShouldReturnBadRequest_WhenEmailAlreadyExists() {
        UserRequest request = UserRequest.builder()
                .name("Ahmet")
                .surname("Yılmaz")
                .email("ahmet@example.com")
                .password("Abc123!@#")
                .phoneNumber("(555) 111-2233")
                .birthDate(LocalDate.of(1990, 5, 15))
                .gender(Gender.MALE)
                .build();

        ResponseMessage<UserResponse> responseMessage = ResponseMessage.<UserResponse>builder()
                .message("Email already exists")
                .httpStatus(HttpStatus.BAD_REQUEST)
                .returnBody(null)
                .build();

        Principal principal = () -> "admin";

        when(userService.saveUser(any(UserRequest.class), eq("Customer"), any(Principal.class)))
                .thenReturn(responseMessage);

        ResponseEntity<ResponseMessage<UserResponse>> response =
                userController.saveUser(request, "Customer", principal);

        assertEquals(responseMessage.getHttpStatus(), response.getStatusCode());
        assertEquals(responseMessage.getMessage(), response.getBody().getMessage());
        assertNull(response.getBody().getReturnBody());
    }

    // ⚠️ POST saveUser - Exception fırlatma
    @Test
    void saveUser_ShouldThrowException_WhenEmailAlreadyExists() {
        UserRequest request = UserRequest.builder()
                .name("Ahmet")
                .surname("Yılmaz")
                .email("ahmet@example.com")
                .password("Abc123!@#")
                .phoneNumber("(555) 111-2233")
                .birthDate(LocalDate.of(1990, 5, 15))
                .gender(Gender.MALE)
                .build();

        Principal principal = () -> "admin";

        when(userService.saveUser(any(), Mockito.anyString(), any()))
                .thenThrow(new IllegalArgumentException("Email already exists"));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                userController.saveUser(request, "Customer", principal)
        );

        assertEquals("Email already exists", exception.getMessage());
    }

    // ✅ GET /{id}/auth - Olumlu ve Olumsuz Senaryolar
    @Test
    @WithMockUser(authorities = {"Admin"})
    void getUserById_ShouldReturnUser_WhenAuthorized() {
        UserResponse mockUser = UserResponse.builder()
                .name("Ali")
                .surname("Veli")
                .email("ali@test.com")
                .phoneNumber("12345")
                .birthDate(null)
                .gender(null)
                .build();

        ResponseMessage<BaseUserResponse> mockResponse = ResponseMessage.<BaseUserResponse>builder()
                .message("User fetched successfully")
                .returnBody(mockUser) // UserResponse, BaseUserResponse’dan türediği için sorun olmaz
                .build();

        Principal mockPrincipal = () -> "admin@test.com";

        when(userService.findUserById(eq(1L), any(Principal.class)))
                .thenReturn(mockResponse);

        ResponseMessage<BaseUserResponse> result =
                userController.getUserById(1L, mockPrincipal);

        BaseUserResponse baseResponse = result.getReturnBody();

        assertEquals("User fetched successfully", result.getMessage());
        assertEquals("Ali", baseResponse.getName());
        assertEquals("Veli", baseResponse.getSurname());
        assertEquals("ali@test.com", baseResponse.getEmail());
        assertEquals("12345", baseResponse.getPhoneNumber());
    }

    // ❌ Olumsuz Senaryo 1: Kullanıcı yetkili değil (AccessDenied)
    @Test
    @WithMockUser(authorities = {"Customer"}) // Admin değil
    void getUserById_ShouldThrowAccessDenied_WhenNotAuthorized() {
        Principal mockPrincipal = () -> "customer@test.com";

        assertThrows(org.springframework.security.access.AccessDeniedException.class, () -> {
            userController.getUserById(1L, mockPrincipal);
        });
    }

    // ❌ Olumsuz Senaryo 2: Kullanıcı bulunamadığında
    @Test
    @WithMockUser(authorities = {"Admin"})
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        Principal mockPrincipal = () -> "admin@test.com";

        when(userService.findUserById(eq(999L), any(Principal.class)))
                .thenThrow(new IllegalArgumentException("User not found"));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userController.getUserById(999L, mockPrincipal);
        });

        assertEquals("User not found", exception.getMessage());
    }

    // ❌ Olumsuz Senaryo 3: Servis hatası / exception fırlatması
    @Test
    @WithMockUser(authorities = {"Admin"})
    void getUserById_ShouldThrowException_WhenServiceFails() {
        Principal mockPrincipal = () -> "admin@test.com";

        when(userService.findUserById(eq(1L), any(Principal.class)))
                .thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userController.getUserById(1L, mockPrincipal);
        });

        assertEquals("Database error", exception.getMessage());
    }

    // ✅ PUT /auth – başarılı senaryo
    @Test
    void updateAuthenticatedUser_ShouldReturnOk_WithUpdatedBody() {
        // arrange
        UserUpdateRequest req = new UserUpdateRequest();
        req.setName("Ali");
        req.setSurname("Veli");
        req.setEmail("ali@veli.com");
        req.setPhoneNumber("(555) 555-5555");
        req.setGender("MALE");
        req.setBirthDate(LocalDate.of(1995, 5, 20));

        UserResponse serviceResp = UserResponse.builder()
                .name("Ali")
                .surname("Veli")
                .email("ali@veli.com")
                .phoneNumber("(555) 555-5555")
                .birthDate(LocalDate.of(1995, 5, 20))
                .gender(Gender.MALE)
                .build();

        Principal principal = () -> "user@example.com";

        when(userService.updateAuthenticatedUser(any(UserUpdateRequest.class), any(Principal.class)))
                .thenReturn(serviceResp);

        // act
        ResponseEntity<UserResponse> response =
                userController.updateAuthenticatedUser(req, principal);

        // assert
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Ali", response.getBody().getName());
        assertEquals("Veli", response.getBody().getSurname());
        assertEquals(Gender.MALE, response.getBody().getGender());
        assertEquals("ali@veli.com", response.getBody().getEmail());
    }

    // ❌ PUT /auth – servis hata fırlatırsa (ör. built-in kullanıcı güncellenemez)
    @Test
    void updateAuthenticatedUser_ShouldPropagateException_WhenServiceThrows() {
        UserUpdateRequest req = new UserUpdateRequest();
        req.setName("Ali");
        req.setSurname("Veli");
        req.setEmail("ali@veli.com");
        req.setPhoneNumber("(555) 555-5555");
        req.setGender("MALE");
        req.setBirthDate(LocalDate.of(1995, 5, 20));

        Principal principal = () -> "user@example.com";

        when(userService.updateAuthenticatedUser(any(UserUpdateRequest.class), any(Principal.class)))
                .thenThrow(new com.cinemax.exception.ConflictException("User cannot be updated"));

        com.cinemax.exception.ConflictException ex =
                assertThrows(com.cinemax.exception.ConflictException.class,
                        () -> userController.updateAuthenticatedUser(req, principal));

        assertEquals("User cannot be updated", ex.getMessage());
    }

}
