package com.cinemax.entity.enums;

import lombok.Getter;

@Getter
public enum MovieStatus {
    COMING_SOON("Coming Soon"),
    IN_THEATERS("In Theaters"),
    PRESALE("Presale"),
    FINISHED("Finished") ;

    private final String label;

    MovieStatus(String label) {
        this.label = label;
    }

}
