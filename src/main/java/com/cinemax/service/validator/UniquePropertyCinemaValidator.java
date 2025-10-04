package com.cinemax.service.validator;

import com.cinemax.exception.ResourceAlreadyExistsException;
import com.cinemax.payload.messages.ErrorMessages;
import com.cinemax.repository.businnes.CinemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniquePropertyCinemaValidator {

    private final CinemaRepository cinemaRepository;

    /**
     * Email ve phone number benzersizlik kontrolü yapar.
     * Eğer biri zaten varsa exception fırlatır.
     */
    public void validateUniqueEmailAndPhone(String email, String phoneNumber) throws ResourceAlreadyExistsException {
        if (cinemaRepository.existsByEmailIgnoreCase(email)) {
            throw new ResourceAlreadyExistsException(
                    String.format(ErrorMessages.CINEMA_EMAIL_EXISTS, email)
            );
        }

        if (cinemaRepository.existsByPhoneNumber(phoneNumber)) {
            throw new ResourceAlreadyExistsException(
                    String.format(ErrorMessages.CINEMA_PHONE_EXISTS, phoneNumber)
            );
        }
    }



}
