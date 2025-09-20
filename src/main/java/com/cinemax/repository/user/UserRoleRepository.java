package com.cinemax.repository.user;

import com.cinemax.entity.concretes.user.UserRole;
import com.cinemax.entity.enums.RoleType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRoleRepository extends CrudRepository<UserRole, Long> {

	Optional<UserRole> findByRoleType(
				RoleType roleType);

    @Query("select r from UserRole r WHERE r.roleType = ?1")
    Optional<Object> findByUserRoleType(RoleType roleType);
}
