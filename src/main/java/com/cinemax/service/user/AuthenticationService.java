package com.cinemax.service.user;

import com.cinemax.entity.concretes.user.User;
import com.cinemax.payload.mappers.UserMapper;
import com.cinemax.payload.messages.ErrorMessages;
import com.cinemax.payload.request.authentication.LoginRequest;
import com.cinemax.payload.request.authentication.RegisterRequest;
import com.cinemax.payload.response.authentication.AuthenticationResponse;
import com.cinemax.payload.response.user.UserResponse;
import com.cinemax.repository.user.UserRepository;
import com.cinemax.security.jwt.JwtUtils;
import com.cinemax.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final JwtUtils jwtUtils;
	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;
	private final UniquePropertyValidator uniquePropertyValidator;
	private final UserMapper userMapper;

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
}
