package com.cinemax.service.helper;

import com.cinemax.exception.CityValidationException;
import com.cinemax.payload.messages.ErrorMessages;
import com.cinemax.payload.request.business.CityRequest;
import org.springframework.stereotype.Component;

@Component
public class CityHelper {

    public void validateCityRequest(CityRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new CityValidationException(ErrorMessages.CITY_NAME_CANNOT_BE_EMPTY);
        }
    }
}