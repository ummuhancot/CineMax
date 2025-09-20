package com.cinemax.controller.user;

import com.cinemax.entity.enums.Gender;
import com.cinemax.payload.request.user.UserRequest;
import com.cinemax.payload.response.business.ResponseMessage;
import com.cinemax.payload.response.user.UserResponse;
import com.cinemax.service.user.UserService;
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
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.time.LocalDate;

import static ch.qos.logback.core.util.AggregationType.NOT_FOUND;
import static javax.security.auth.callback.ConfirmationCallback.OK;
import static org.junit.jupiter.api.Assertions.*;
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

    // ✅ Pozitif senaryo: Geçerli bir kullanıcı isteği gönderildiğinde 201 CREATED dönmeli
    @Test
    void saveUser_ShouldReturn201Created_WhenRequestIsValid() {
        // Arrange
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

        Principal principal = () -> "admin"; // sahte principal

        Mockito.when(userService.saveUser(Mockito.any(UserRequest.class), Mockito.eq("Customer"), Mockito.any(Principal.class)))
                .thenReturn(responseMessage);

        // Act
        ResponseEntity<ResponseMessage<UserResponse>> response =
                userController.saveUser(request, "Customer", principal);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User created successfully", response.getBody().getMessage());
        assertEquals("ahmet@example.com", response.getBody().getReturnBody().getEmail());
        assertEquals("Ahmet", response.getBody().getReturnBody().getName());
    }

    // ❌ Negatif senaryo: Aynı e-posta ile kullanıcı kaydı yapılmaya çalışıldığında 400 BAD_REQUEST dönmeli
    @Test
    void saveUser_ShouldReturnBadRequest_WhenEmailAlreadyExists() {
        // Arrange
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

        Mockito.when(userService.saveUser(Mockito.any(UserRequest.class), Mockito.eq("Customer"), Mockito.any(Principal.class)))
                .thenReturn(responseMessage);

        // Act
        ResponseEntity<ResponseMessage<UserResponse>> response =
                userController.saveUser(request, "Customer", principal);

        // Assert
        assertEquals(responseMessage.getHttpStatus(), response.getStatusCode());
        assertEquals(responseMessage.getMessage(), response.getBody().getMessage());
        assertNull(response.getBody().getReturnBody());
    }

    // ⚠ Exception senaryosu: Servis e-posta zaten varsa exception fırlatıyor
    @Test
    void saveUser_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
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

        Mockito.when(userService.saveUser(Mockito.any(), Mockito.anyString(), Mockito.any()))
                .thenThrow(new IllegalArgumentException("Email already exists"));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                userController.saveUser(request, "Customer", principal)
        );

        assertEquals("Email already exists", exception.getMessage());
    }


}
