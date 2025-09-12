package com.cinemax.entity.enums;

import lombok.Getter;

@Getter
public enum Gender {
	MALE("Male"), FEMALE("Female"), OTHER("Other");

	private final String name;

	Gender(String name) {
		this.name = name;
	}
}
