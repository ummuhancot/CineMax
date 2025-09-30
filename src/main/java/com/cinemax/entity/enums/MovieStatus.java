package com.cinemax.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MovieStatus {
    COMING_SOON(0),
    IN_THEATERS(1),
    PRESALE(2);

    private final int code;

    MovieStatus(int code) {
        this.code = code;
    }

    /** JSON'a yazarken 0/1/2 olarak serileştirir */
    @JsonValue
    public int getCode() {
        return code;
    }

    /** JSON'dan 0/1/2 (veya String sayısal) geldiğinde enum'a çevirir */
    @JsonCreator
    public static MovieStatus from(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("MovieStatus cannot be null");
        }
        // 1) Sayısal (Integer / Long / String sayı) olarak geldiyse
        if (value instanceof Number) {
            return fromCode(((Number) value).intValue());
        }
        String s = value.toString().trim();
        if (s.matches("^-?\\d+$")) {
            return fromCode(Integer.parseInt(s));
        }
        // 2) İsim (coming_soon / COMING_SOON / Coming_Soon) olarak geldiyse
        return fromName(s);
    }

    /** 0/1/2 değerlerinden enum'a çevirir */
    public static MovieStatus fromCode(int code) {
        for (MovieStatus s : values()) {
            if (s.code == code) return s;
        }
        throw new IllegalArgumentException("Unknown MovieStatus code: " + code);
    }

    /** İsimden (case-insensitive) enum'a çevirir: "coming_soon", "COMING_SOON", "In_Theaters" vb. */
    public static MovieStatus fromName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("MovieStatus name cannot be null");
        }
        String normalized = name.trim().toUpperCase();
        // Bazı muhtemel varyasyonları normalize et
        normalized = normalized.replace(' ', '_');
        switch (normalized) {
            case "COMING_SOON":    return COMING_SOON;
            case "IN_THEATERS":    return IN_THEATERS;
            case "PRESALE":        return PRESALE;
            default:
                throw new IllegalArgumentException("Unknown MovieStatus name: " + name);
        }
    }
}
/*
Notlar

Bu enum, önceki sürümdeki code tabanlı JSON serileştirmeyi bozmadan genişletir: İsterseniz client tarafı "status": 1 veya "status": "IN_THEATERS" gönderebilir; ikisi de doğru şekilde parse edilir.

Veritabanında nasıl saklayacağınız (STRING veya INT) entity tarafındaki anotasyon/converter ile belirlenir. Bu enum her iki yönteme de uygundur.
 */