package com.cinemax.service.user;

import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.mappers.UserMapper;
import com.cinemax.payload.messages.ErrorMessages;
import com.cinemax.payload.response.user.UserResponse;
import com.cinemax.repository.user.UserRepository;
import com.cinemax.service.helper.PageableHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PageableHelper pageableHelper;
	private final UserMapper userMapper;


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
}
