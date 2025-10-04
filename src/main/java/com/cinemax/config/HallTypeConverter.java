package com.cinemax.config;

import com.cinemax.entity.enums.HallType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;

@Converter(autoApply = true)
public class HallTypeConverter implements AttributeConverter<HallType, String> {
    @Override
    public String convertToDatabaseColumn(HallType attribute) {
        return attribute != null ? attribute.name() : null;
    }

    @Override
    public HallType convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return Arrays.stream(HallType.values())
                .filter(e -> e.name().equalsIgnoreCase(dbData))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown value: " + dbData));
    }
}
