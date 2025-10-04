package com.cinemax.entity.enums;

import lombok.Getter;

@Getter
public enum HallType {
    IMAX("IMAX"),
    VIP("VIP"),
    STANDARD("Standard"),
    THREE_D("3D");

    private final String label;

    HallType(String label) {
        this.label = label;
    }
}

