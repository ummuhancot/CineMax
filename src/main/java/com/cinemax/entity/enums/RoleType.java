package com.cinemax.entity.enums;

import lombok.Getter;

@Getter
public enum RoleType {
	ADMIN("Admin"), MANAGER("Manager"), CUSTOMER("Customer");

	private final String name;

	RoleType(String name) {
		this.name = name;
	}
}
