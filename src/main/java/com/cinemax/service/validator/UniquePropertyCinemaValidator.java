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


    /**
     * ✅ UPDATE işlemi için:
     * Güncellenen Cinema haricinde aynı email veya phone number başka bir kayıt tarafından kullanılıyor mu kontrol eder.
     * @param cinemaId güncellenen kaydın ID'si
     * @param email yeni email
     * @param phoneNumber yeni telefon numarası
     */
    public void validateUniqueEmailAndPhoneForUpdate(Long cinemaId, String email, String phoneNumber)
            throws ResourceAlreadyExistsException {

        // Email başka bir sinemada varsa hata ver
        if (cinemaRepository.existsByEmailIgnoreCase(email)) {
            Long existingId = cinemaRepository.findByEmailIgnoreCase(email)
                    .map(c -> c.getId())
                    .orElse(null);

            if (existingId != null && !existingId.equals(cinemaId)) {
                throw new ResourceAlreadyExistsException(
                        String.format(ErrorMessages.CINEMA_EMAIL_EXISTS, email)
                );
            }
        }

        // Phone başka bir sinemada varsa hata ver
        if (cinemaRepository.existsByPhoneNumber(phoneNumber)) {
            Long existingId = cinemaRepository.findByPhoneNumber(phoneNumber)
                    .map(c -> c.getId())
                    .orElse(null);

            if (existingId != null && !existingId.equals(cinemaId)) {
                throw new ResourceAlreadyExistsException(
                        String.format(ErrorMessages.CINEMA_PHONE_EXISTS, phoneNumber)
                );
            }
        }
    }

}
