package com.cinemax.service.user;

import com.cinemax.entity.concretes.user.UserRole;
import com.cinemax.entity.enums.RoleType;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.messages.ErrorMessages;
import com.cinemax.repository.user.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRoleService {

	private final UserRoleRepository userRoleRepository;

	public UserRole getByRoleType(
				RoleType roleType) {
		return userRoleRepository.findByRoleType(roleType)
					       .orElseThrow(()->new ResourceNotFoundException(String.format(ErrorMessages.ROLE_NOT_FOUND, roleType.getName())));
	}


}
