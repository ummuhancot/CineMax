package com.cinemax.service.bussines;

import com.cinemax.entity.concretes.business.Cinema;
import com.cinemax.entity.concretes.business.Hall;

import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.mappers.HallMapper;
import com.cinemax.payload.messages.ErrorMessages;
import com.cinemax.payload.request.business.HallRequest;
import com.cinemax.payload.response.business.HallResponse;
import com.cinemax.repository.businnes.CinemaRepository;
import com.cinemax.repository.businnes.HallRepository;
import com.cinemax.service.helper.HallHelper;
import com.cinemax.service.validator.HallValidator;
import com.cinemax.util.HallSeatCache;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

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

        // Kaydedilen hall için seat ekle
        addSeatsToHall(saved);

        // Response dön
        return hallMapper.convertHallToResponse(saved);
    }
    /**
     * Hall entity'sine göre seat oluşturur ve cache'e ekler.
     */
    // Hall için seat ekleme (entity olmadan, transient cache)
    private void addSeatsToHall(Hall hall) {
        List<String> seats = new ArrayList<>();
        for (int i = 1; i <= hall.getSeatCapacity(); i++) {
            seats.add("Seat-" + i);
        }
        HallSeatCache.addSeats(hall.getId(), seats); // HallSeatCache: id -> seat list

    }


    public HallResponse getHallById(Long hallId) {
        // Find the hall or throw an exception if not found
        Hall hall = hallRepository.findById(hallId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(ErrorMessages.HALL_NOT_FOUND, hallId)));
        // Convert to response and return
        return hallMapper.convertHallToResponse(hall);
    }


    public HallResponse deleteHall(Long hallId) {
        // Find the hall or throw exception if not found
        Hall hall = hallRepository.findById(hallId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(ErrorMessages.HALL_NOT_FOUND, hallId)
                ));

        // Delete the hall
        hallRepository.delete(hall);

        // Convert to response and return deleted hall data
        return hallMapper.convertHallToResponse(hall);
    }

    public List<HallResponse> getAllHalls() {
        // Fetch all halls from the repository
        List<Hall> halls = hallRepository.findAll();

        // Convert entities to response DTOs
        return halls.stream()
                .map(hallMapper::convertHallToResponse)
                .toList();
    }


    @Transactional
    public HallResponse updateHall(Long id, HallRequest request) {
        Hall existingHall = hallRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.HALL_NOT_FOUND, id)
                ));

        Cinema cinema = cinemaRepository.findById(request.getCinemaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.CINEMA_NOT_FOUND, request.getCinemaId())
                ));

        Hall updatedHall = hallMapper.updateHallFromRequest(existingHall, request, cinema);
        Hall saved = hallRepository.save(updatedHall);

        return hallMapper.convertHallToResponse(saved);
    }

    /**
     * Hall ID'ye göre seat listesini döner.
     * Hall bulunamazsa ResourceNotFoundException fırlatır.
     */
    public List<String> getSeatsForHall(Long hallId) {
        if (!HallSeatCache.hasHall(hallId)) {
            // Hall repository’den kontrol et
            Hall hall = hallRepository.findById(hallId)
                    .orElseThrow(() -> new ResourceNotFoundException("Hall not found with id: " + hallId));

            // Hall bulunduysa seat ekle
            addSeatsToHall(hall);
        }
        return HallSeatCache.getSeats(hallId);
    }




}
