package com.cinemax.payload.mappers;

import com.cinemax.entity.concretes.user.User;
import com.cinemax.entity.enums.RoleType;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.messages.ErrorMessages;
import com.cinemax.payload.request.abstracts.BaseUserRequest;
import com.cinemax.payload.request.authentication.RegisterRequest;
import com.cinemax.payload.response.user.UserResponse;
import com.cinemax.service.user.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

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
					       .userRole(userRoleService.getByRoleType(RoleType.Customer))
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

    public User mapUserRequestToUser(BaseUserRequest userRequest, String userRole) {
        User user = User.builder()
                .name(userRequest.getName())
                .surname(userRequest.getSurname())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .birthDate(userRequest.getBirthDate())
                .gender(userRequest.getGender())
                .build();


        if(userRole.equalsIgnoreCase(RoleType.Admin.getName())){
            if(Objects.equals(userRequest.getEmail(),"Admin")){
                user.setBuiltIn(true);
            }
            user.setUserRole(userRoleService.getByRoleType(RoleType.Admin));

        } else if (userRole.equalsIgnoreCase(RoleType.Manager.getName())) {
            user.setUserRole(userRoleService.getByRoleType(RoleType.Manager));
        }
        else if (userRole.equalsIgnoreCase(RoleType.Customer.getName())) {
            user.setUserRole(userRoleService.getByRoleType(RoleType.Customer));
        }
        else {
            throw new ResourceNotFoundException(
                    String.format(ErrorMessages.NOT_HAVE_EXPECTED_ROLE_USER, userRole));
        }

        return user;
    }


}
