package com.cinemax.entity.enums;

import lombok.Getter;

@Getter
public enum RoleType {
	Admin("Admin"), Manager("Manager"), Customer("Customer");

	private final String name;

	RoleType(String name) {
		this.name = name;
	}
}
