package com.cinemax.repository.user;

import com.cinemax.entity.concretes.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(
				String email);

	boolean existsByEmail(
				String email);

	boolean existsByPhoneNumber(
				String phoneNumber);


	Page<User> findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneNumberContainingIgnoreCase(
				String name,
				String surname,
				String email,
				String phoneNumber,
				Pageable pageable);
}
