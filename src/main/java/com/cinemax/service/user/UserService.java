package com.cinemax.service.user;

import com.cinemax.entity.concretes.user.User;
import com.cinemax.entity.enums.Gender;
import com.cinemax.exception.BuiltInUserException;
import com.cinemax.exception.ConflictException;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.mappers.UserMapper;
import com.cinemax.payload.messages.ErrorMessages;
import com.cinemax.payload.request.authentication.UserUpdateRequest;
import com.cinemax.payload.response.user.UserResponse;
import com.cinemax.repository.user.UserRepository;
import com.cinemax.service.helper.PageableHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;

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


	//U7 - Deletes the authenticated user from the user repository
	public ResponseEntity<?> deleteAuthenticatedUser(UserDetails userDetails) {
		// Cast the userDetails to User type
		User user = (User) userDetails;
		// Check for built-in and related fields in the user object
		checkBuiltInBeforeDeletion(user);
		// Delete the user from the user repository
		userRepository.delete(user);
		// Return ResponseEntity with status 200 OK
		return ResponseEntity.ok().build();
	}

	/**
	 * Kullanıcının built-in (sistem tarafından oluşturulmuş) olup olmadığını kontrol eder.
	 * Eğer built-in ise silme işlemi engellenir ve uygun exception fırlatılır.
	 *
	 * @param user Silinmek istenen kullanıcı
	 * @throws com.cinemax.exception.BuiltInUserException Kullanıcı built-in ise fırlatılır
	 */
	private void checkBuiltInBeforeDeletion(User user) {
		if (Boolean.TRUE.equals(user.getBuiltIn())) {
			throw new BuiltInUserException(ErrorMessages.USER_BUILT_IN);
		}
	}

}


