package com.cinemax.service.helper;

import com.cinemax.entity.concretes.business.Cinema;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.messages.ErrorMessages;
import com.cinemax.repository.businnes.CinemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HallHelper {

    // Cinema bulma ve exception fırlatma
    public static Cinema findCinemaOrThrow(Long cinemaId, CinemaRepository cinemaRepository) {
        return cinemaRepository.findById(cinemaId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.CINEMA_NOT_FOUND, cinemaId)
                ));
    }

}
