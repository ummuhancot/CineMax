package com.cinemax.service.bussines;

import com.cinemax.entity.concretes.business.Cinema;
import com.cinemax.entity.concretes.business.Hall;
import com.cinemax.payload.mappers.HallMapper;
import com.cinemax.payload.messages.ErrorMessages;
import com.cinemax.payload.request.business.HallRequest;
import com.cinemax.payload.response.business.HallResponse;
import com.cinemax.repository.businnes.CinemaRepository;
import com.cinemax.repository.businnes.HallRepository;
import com.cinemax.service.helper.HallHelper;
import com.cinemax.service.validator.HallValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HallService {

    private final HallRepository hallRepository;
    private final HallMapper hallMapper;
    private final CinemaRepository cinemaRepository;


    public HallResponse saveHall(HallRequest request) {
        // Helper ile Cinema’yı bul
        Cinema cinema = HallHelper.findCinemaOrThrow(request.getCinemaId(), cinemaRepository);

        // Helper ile aynı isimde salon var mı kontrol et
        HallValidator.checkHallUnique(request.getName(), cinema.getId(), hallRepository);

        // Hall entity oluştur
        Hall hall = hallMapper.convertRequestToHall(request, cinema);

        // Kaydet
        Hall saved = hallRepository.save(hall);

        // Response dön
        return hallMapper.convertHallToResponse(saved);
    }

    public HallResponse getHallById(Long hallId) {
        // Find the hall or throw an exception if not found
        Hall hall = hallRepository.findById(hallId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(ErrorMessages.HALL_NOT_FOUND, hallId)));
        // Convert to response and return
        return hallMapper.convertHallToResponse(hall);
    }


}
