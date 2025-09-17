package com.cinemax.repository.user;

import com.cinemax.entity.concretes.user.UserRole;
import com.cinemax.entity.enums.RoleType;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRoleRepository extends CrudRepository<UserRole, Long> {

	Optional<UserRole> findByRoleType(
				RoleType roleType);
}
