package com.cinemax.service.validator;

import com.cinemax.exception.BadRequestException;
import com.cinemax.repository.businnes.ShowTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class ShowTimeValidator {

    private final ShowTimeRepository showTimeRepository;

    public void checkOverlap(Long hallId, LocalDate date, LocalTime startTime, LocalTime endTime) {

        boolean exists = showTimeRepository.existsByHallIdAndDateAndTimeOverlap(hallId, date, startTime, endTime);

        if (exists) {
            throw new BadRequestException("This showtime overlaps with an existing one in the same hall.");
        }
    }

}