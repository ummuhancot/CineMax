package com.cinemax.controller.user;

import com.cinemax.payload.response.user.UserResponse;
import com.cinemax.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/admin")
	@PreAuthorize("hasAnyAuthority('Admin','Manager')")
	public ResponseEntity<Page<UserResponse>> getAllUsersWithQuery(
				@RequestParam(required = false) String q,
				@RequestParam(defaultValue = "0", required = false) int page,
				@RequestParam(defaultValue = "20", required = false) int size,
				@RequestParam(defaultValue = "id", required = false) String sort,
				@RequestParam(defaultValue = "desc", required = false) String type) {
		return ResponseEntity.ok(userService.getAllUsersWithQuery(q, page, size, sort, type));
	}

}
