package com.cinemax.controller.user;

import com.cinemax.payload.response.user.UserResponse;
import com.cinemax.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

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

}
