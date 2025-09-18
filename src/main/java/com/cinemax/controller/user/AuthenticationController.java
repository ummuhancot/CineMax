package com.cinemax.controller.user;

import com.cinemax.payload.request.authentication.LoginRequest;
import com.cinemax.payload.request.authentication.RegisterRequest;
import com.cinemax.payload.request.authentication.UserUpdateRequest;
import com.cinemax.payload.response.authentication.AuthenticationResponse;
import com.cinemax.payload.response.user.UserResponse;
import com.cinemax.service.user.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthenticationController {

	private final AuthenticationService authenticationService;

	@PostMapping("/login")
	public ResponseEntity<AuthenticationResponse> authenticate(
				@RequestBody @Valid LoginRequest loginRequest) {
		return ResponseEntity.ok(authenticationService.authenticate(loginRequest));
	}

	@PostMapping("/register")
	public ResponseEntity<UserResponse> register(
				@RequestBody @Valid RegisterRequest registerRequest) {
		return new ResponseEntity<>(authenticationService.register(registerRequest), HttpStatus.CREATED);
	}

    @PutMapping("/auth")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CUSTOMER')")
    public ResponseEntity<UserResponse> updateAuthenticatedUser(
            @RequestBody @Valid UserUpdateRequest request,
            Principal principal){

        UserResponse response = authenticationService.updateAuthenticatedUser(request, principal);
        return ResponseEntity.ok(response);
    }
}
