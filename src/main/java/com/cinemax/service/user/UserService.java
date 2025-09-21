package com.cinemax.service.user;

import com.cinemax.entity.concretes.user.User;
import com.cinemax.entity.enums.Gender;
import com.cinemax.exception.ConflictException;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.mappers.UserMapper;
import com.cinemax.payload.messages.ErrorMessages;
import com.cinemax.payload.messages.SuccessMessages;
import com.cinemax.payload.request.authentication.ResetPasswordRequest;
import com.cinemax.payload.request.authentication.UserUpdateRequest;
import com.cinemax.payload.request.user.UserRequest;
import com.cinemax.payload.response.abstracts.BaseUserResponse;
import com.cinemax.payload.response.business.ResponseMessage;
import com.cinemax.payload.response.user.UserResponse;
import com.cinemax.repository.user.UserRepository;
import com.cinemax.service.helper.MethodHelper;
import com.cinemax.service.helper.PageableHelper;
import com.cinemax.service.validator.UniquePropertyValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PageableHelper pageableHelper;
	private final UserMapper userMapper;
    private final UniquePropertyValidator uniquePropertyValidator;
    private final MethodHelper methodHelper;
    private final PasswordEncoder passwordEncoder;

	public Page<UserResponse> getAllUsersWithQuery(
				String q,
				int page,
				int size,
				String sort,
				String type) {
		Pageable pageable = pageableHelper.getPageable(page, size, sort, type);
		Page<UserResponse> userResponses;
		if (q == null || q.isBlank()) {
			userResponses = userRepository.findAll(pageable)
						                .map(userMapper::mapUserToUserResponse);
		} else {
			userResponses = userRepository.findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneNumberContainingIgnoreCase(q, q, q, q, pageable)
						                .map(userMapper::mapUserToUserResponse);
		}

		if (!userResponses.hasContent()) {
			throw new ResourceNotFoundException(String.format(ErrorMessages.USER_NOT_FOUND_WITH_QUERY, q == null ? "" : q));
		}

		return userResponses;
	}

    public  UserResponse updateAuthenticatedUser(UserUpdateRequest request, Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.USER_NOT_FOUND_MAIL,email)));

        if (Boolean.TRUE.equals(user.getBuiltIn())) {
            throw new ConflictException(ErrorMessages.USER_UPDATE_FORBIDDEN);

        }
        user.setName(request.getName());
        user.setSurname(request.getSurname());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());

        if (request.getGender() != null) {
            user.setGender(Gender.valueOf(request.getGender().toUpperCase()));
        }

        user.setBirthDate(request.getBirthDate());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        return UserResponse.builder()
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender())
                .birthDate(user.getBirthDate())
                .build();
    }

	public UserResponse deleteUserByIdAsAdminOrManager(
				Long id,
				Principal principal) {
		String email = principal.getName();
		if (userRepository.findByEmail(email)
					    .orElseThrow(()->new ResourceNotFoundException(String.format(ErrorMessages.USER_NOT_FOUND_MAIL, email)))
					    .getId()
					    .equals(id)) {
			throw new ConflictException(ErrorMessages.USER_DELETE_SELF_FORBIDDEN);
		}
		User user = userRepository.findById(id)
					            .orElseThrow(()->new ResourceNotFoundException(String.format(ErrorMessages.USER_NOT_FOUND_ID, id)));
		if (Boolean.TRUE.equals(user.getBuiltIn())) {
			throw new ConflictException(ErrorMessages.USER_DELETE_FORBIDDEN);
		}
		userRepository.delete(user);
		return userMapper.mapUserToUserResponse(user);
	}

    public ResponseMessage<UserResponse> saveUser(@Valid UserRequest userRequest, String userRole,Principal principal) {
        String emailToCreate = userRequest.getEmail();

        // Eğer kullanıcı zaten varsa hata fırlat
        userRepository.findByEmail(emailToCreate).ifPresent(existingUser -> {
            throw new ConflictException(String.format(ErrorMessages.USER_ALREADY_EXISTS, emailToCreate));
        });

        uniquePropertyValidator.checkDuplication(
                userRequest.getEmail(),
                userRequest.getPhoneNumber()
        );
        User userToSave = userMapper.mapUserRequestToUser(userRequest, userRole);

        User savedUser = userRepository.save(userToSave);

        UserResponse userResponse = userMapper.mapUserToUserResponse(savedUser);

        ResponseMessage<UserResponse> response = ResponseMessage.<UserResponse>builder()
                .message(SuccessMessages.AUTH_USER_CREATED_SUCCESS + " " + SuccessMessages.USER_AUTH_FETCHED_SUCCESS)
                .returnBody(userResponse)
                .httpStatus(HttpStatus.CREATED)
                .build();
        return response;

    }


    public ResponseMessage<BaseUserResponse> findUserById(Long id, Principal principal) {
        String email = principal.getName();
        if (userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.USER_NOT_FOUND_MAIL, email)))
                .getId()
                .equals(id)) {
            throw new ConflictException(ErrorMessages.USERS_FETCH_FAILED);
        }

        User user = methodHelper.isUserExist(id);
        return ResponseMessage.<BaseUserResponse>builder()
                .message(SuccessMessages.USER_FETCHED_SUCCESS)
                .returnBody(userMapper.mapUserToUserResponse(user))
                .httpStatus(HttpStatus.OK)
                .build();
    }
    @Transactional
    public void resetPassword(String email, ResetPasswordRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email " + email));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password does not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }




}
