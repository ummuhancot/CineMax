package com.cinemax.service.validator;

import com.cinemax.exception.ConflictException;
import com.cinemax.payload.messages.ErrorMessages;
import com.cinemax.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniquePropertyValidator {

	private final UserRepository userRepository;

	public void checkDuplication(
				String email,
				String phone) {
		if (userRepository.existsByEmail(email)) {
			throw new ConflictException(ErrorMessages.EMAIL_ALREADY_EXISTS);
		}
		if (userRepository.existsByPhoneNumber(phone)) {
			throw new ConflictException(ErrorMessages.PHONE_ALREADY_EXISTS);
		}
	}

}
