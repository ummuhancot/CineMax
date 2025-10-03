package com.cinemax.service.validator;

import com.cinemax.payload.messages.ErrorMessages;
import com.cinemax.repository.businnes.HallRepository;
import org.springframework.stereotype.Component;

@Component
public class HallValidator {

    // Aynı isimde salon var mı kontrol et
    public static void checkHallUnique(String hallName, Long cinemaId, HallRepository hallRepository) {
        boolean exists = hallRepository.existsByNameAndCinemaId(hallName, cinemaId);
        if (exists) {
            throw new RuntimeException(
                    String.format(ErrorMessages.HALL_ALREADY_EXISTS, hallName)
            );
        }
    }
}
