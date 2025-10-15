package com.cinemax.service.helper;

import com.cinemax.exception.CityValidationException;
import com.cinemax.exception.ConflictException;
import com.cinemax.payload.messages.ErrorMessages;
import com.cinemax.payload.request.business.CityRequest;
import com.cinemax.repository.businnes.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@RequiredArgsConstructor
public class CityHelper {

    private final CityRepository cityRepository;

    public void validateCityRequest(CityRequest request) {
        // Burada request validation varsa zaten yapılıyor
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException(ErrorMessages.CITY_NAME_CANNOT_BE_BLANK);
        }

        // 🔹 City isminin benzersizliğini kontrol et
        if (cityRepository.existsByName(request.getName())) {
            throw new ConflictException(
                    String.format(ErrorMessages.CITY_ALREADY_EXISTS, request.getName())
            );
        }
    }
}