package com.cinemax.entity.concretes.user;

import com.cinemax.entity.enums.Gender;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String surname;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false, unique = true)
	private String phoneNumber;

	@Column(nullable = false)
	private LocalDate birthDate;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	@ManyToOne
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private UserRole userRole;

	@Column(nullable = false)
	private Boolean builtIn = false;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	@Column
	private String resetPasswordCode;

	@PrePersist
	private void onCreate() {
		this.createdAt = LocalDateTime.now();
	}

	@PreUpdate
	private void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
}
