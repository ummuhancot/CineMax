package com.cinemax.controller.user;

import com.cinemax.payload.request.authentication.LoginRequest;
import com.cinemax.payload.request.authentication.RegisterRequest;
import com.cinemax.payload.response.authentication.AuthenticationResponse;
import com.cinemax.payload.response.user.UserResponse;
import com.cinemax.service.user.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthenticationController {

	private final AuthenticationService authenticatonService;

	@PostMapping("/login")
	public ResponseEntity<AuthenticationResponse> authenticate(
				@RequestBody @Valid LoginRequest loginRequest) {
		return ResponseEntity.ok(authenticatonService.authenticate(loginRequest));
	}

	@PostMapping("/register")
	public ResponseEntity<UserResponse> register(
				@RequestBody @Valid RegisterRequest registerRequest) {
		return new ResponseEntity<>(authenticatonService.register(registerRequest), HttpStatus.CREATED);
	}

}
