package com.cinemax.controller.user;

import com.cinemax.entity.concretes.user.User;
import com.cinemax.payload.request.authentication.ResetPasswordRequest;
import com.cinemax.payload.request.authentication.UserUpdateRequest;
import com.cinemax.payload.request.user.UserRequest;
import com.cinemax.payload.response.abstracts.BaseUserResponse;
import com.cinemax.payload.response.business.ResponseMessage;
import com.cinemax.payload.response.user.UserResponse;
import com.cinemax.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/admin")
	@PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
	public ResponseEntity<Page<UserResponse>> getAllUsersWithQuery(
				@RequestParam(required = false) String q,
				@RequestParam(defaultValue = "0", required = false) int page,
				@RequestParam(defaultValue = "20", required = false) int size,
				@RequestParam(defaultValue = "id", required = false) String sort,
				@RequestParam(defaultValue = "desc", required = false) String type) {
		return ResponseEntity.ok(userService.getAllUsersWithQuery(q, page, size, sort, type));
	}


    @PutMapping("/auth")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CUSTOMER')")
    public ResponseEntity<UserResponse> updateAuthenticatedUser(
            @RequestBody @Valid UserUpdateRequest request,
            Principal principal){

        UserResponse response = userService.updateAuthenticatedUser(request, principal);
        return ResponseEntity.ok(response);
    }

	/*
	 U07 /auth
	 delete - it will delete the authenticated user
	 /api/users/auth
	 //giriş yapmış kullanıcıyı siler, islemin sonucunu belirten ResponseEntity return eder
	*/
	@DeleteMapping("/auth")
	public ResponseEntity<?> deleteAuthenticatedUser( @AuthenticationPrincipal UserDetails userDetails){
		return userService.deleteAuthenticatedUser(userDetails);
	}
  
	@DeleteMapping("/{id}/admin")
	@PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
	public ResponseEntity<UserResponse> deleteUserByIdAsAdminOrManager(@PathVariable Long id,
	                                                                   Principal principal) {
		return ResponseEntity.ok(userService.deleteUserByIdAsAdminOrManager(id , principal));
	}

    @PostMapping("/auth/{userRole}")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager',Customer)")
    public ResponseEntity<ResponseMessage<UserResponse>> saveUser(
            @RequestBody @Valid UserRequest userRequest,
            @PathVariable String userRole,
            Principal principal) {
        ResponseMessage<UserResponse> response = userService.saveUser(userRequest, userRole,principal);

        // HTTP 201 ile response döndür
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/auth")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public ResponseMessage<BaseUserResponse> getUserById(@PathVariable Long id,Principal principal){
        return userService.findUserById(id,principal);
    }
    @PostMapping("/reset-password")
    public void resetPassword(@AuthenticationPrincipal(expression = "username") String email,
                              @Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(email, request);
    }
    @PutMapping("/{id}/admin")
    public User updateUserByAdmin(@PathVariable Long id,
                                  @Valid @RequestBody UserUpdateRequest req,
                                  Authentication auth) {
        return userService.updateUserByAdmin(id, req, auth);
    }
}
