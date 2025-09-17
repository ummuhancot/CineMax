package com.cinemax.payload.mappers;

import com.cinemax.entity.concretes.user.User;
import com.cinemax.entity.concretes.user.UserRole;
import com.cinemax.entity.enums.RoleType;
import com.cinemax.payload.request.authentication.RegisterRequest;
import com.cinemax.payload.response.user.UserResponse;
import com.cinemax.service.user.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

	private final PasswordEncoder passwordEncoder;
	private final UserRoleService userRoleService;

	public User mapRegisterRequestToUser(
				RegisterRequest registerRequest) {
		return User.builder()
					       .name(registerRequest.getName())
					       .surname(registerRequest.getSurname())
					       .password(passwordEncoder.encode(registerRequest.getPassword()))
					       .email(registerRequest.getEmail())
					       .phoneNumber(registerRequest.getPhoneNumber())
					       .birthDate(registerRequest.getBirthDate())
					       .gender(registerRequest.getGender())
					       .userRole(userRoleService.getByRoleType(RoleType.CUSTOMER))
					       .build();
	}

	public UserResponse mapUserToUserResponse(
				User user) {
		return UserResponse.builder()
					       .name(user.getName())
					       .surname(user.getSurname())
					       .email(user.getEmail())
					       .phoneNumber(user.getPhoneNumber())
					       .birthDate(user.getBirthDate())
					       .gender(user.getGender())
					       .build();
	}
}
