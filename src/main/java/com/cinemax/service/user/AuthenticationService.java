package com.cinemax.service.user;

import com.cinemax.entity.concretes.user.User;
import com.cinemax.entity.enums.Gender;
import com.cinemax.entity.enums.RoleType;
import com.cinemax.exception.ConflictException;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.mappers.UserMapper;
import com.cinemax.payload.messages.ErrorMessages;
import com.cinemax.payload.request.authentication.ForgotPasswordRequest;
import com.cinemax.payload.request.authentication.LoginRequest;
import com.cinemax.payload.request.authentication.RegisterRequest;
import com.cinemax.payload.request.authentication.UserUpdateRequest;
import com.cinemax.payload.response.authentication.AuthenticationResponse;
import com.cinemax.payload.response.user.UserResponse;
import com.cinemax.repository.user.UserRepository;
import com.cinemax.security.jwt.JwtUtils;
import com.cinemax.service.EmailService;
import com.cinemax.service.validator.UniquePropertyValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final JwtUtils jwtUtils;
	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;
	private final UniquePropertyValidator uniquePropertyValidator;
	private final UserMapper userMapper;
	private final EmailService emailService;
	private final PasswordEncoder passwordEncoder;
	private final UserRoleService userRoleService;

	public AuthenticationResponse authenticate(
				LoginRequest loginRequest) {
		String email = loginRequest.getEmail();
		char[] password = loginRequest.getPassword()
					                  .toCharArray();

		try {
			Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, new String(password)));

			SecurityContextHolder.getContext()
						.setAuthentication(authentication);

			String token = jwtUtils.generateJwtToken(authentication);


			return AuthenticationResponse.builder()
						       .authToken(token)
						       .build();
		} catch (AuthenticationException e) {
			throw new AuthenticationException(ErrorMessages.LOGIN_FAILED) {
			};
		} finally {
			Arrays.fill(password, '\0');
		}

	}

	 public UserResponse register(
	 RegisterRequest registerRequest) {
	 uniquePropertyValidator.checkDuplication(registerRequest.getEmail(), registerRequest.getPhoneNumber());
	 User userToSave = userMapper.mapRegisterRequestToUser(registerRequest);
	 return userMapper.mapUserToUserResponse(userRepository.save(userToSave));
	 }

	/** U3 - forgotPassword start */
	public ResponseEntity<?> forgotPassword(@Valid ForgotPasswordRequest request) {
		//Find the user by email
		User user = userRepository.findByEmail(request.getEmail()).orElse(null);
		//If the user exists
		if(user!=null){
			//Generate a reset password code
			String resetPasswordCode = UUID.randomUUID().toString();
			//Save the reset password code to the user object
			user.setResetPasswordCode(resetPasswordCode);
			userRepository.save(user);
			//Send the reset password code to the user's email
			sendResetPasswordCode(user.getEmail(), resetPasswordCode);
		}
		//Return the HTTP response entity with status OK
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	private void sendResetPasswordCode(String email, String resetPasswordCode){
		emailService.sendResetPasswordEmail(email, resetPasswordCode);
	}

	/** U3 - forgotPassword end */
}
