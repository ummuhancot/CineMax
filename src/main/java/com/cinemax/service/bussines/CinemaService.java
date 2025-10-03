package com.cinemax.service.bussines;

import com.cinemax.entity.concretes.business.Cinema;
import com.cinemax.entity.concretes.business.City;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.mappers.CinemaMapper;
import com.cinemax.payload.messages.ErrorMessages;
import com.cinemax.payload.request.business.CinemaRequest;
import com.cinemax.payload.response.business.CinemaHallResponse;
import com.cinemax.payload.response.business.CinemaResponse;
import com.cinemax.repository.businnes.CinemaRepository;
import com.cinemax.repository.businnes.CityRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CinemaService {

    private final CinemaRepository cinemaRepository;
    private final CinemaMapper cinemaMapper;
    private final CityRepository cityRepository;

    // GET /api/cinemas?city=&specialHall=
    public List<CinemaHallResponse> getCinemas(String city, String specialHall) {
        List<Cinema> cinemas;

        if (city != null || specialHall != null) {
            // Filtre uygulanmış, uygun sinema yoksa exception
            try {
                cinemas = cinemaRepository.findCinemasByCityAndSpecialHall(city, specialHall)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                ErrorMessages.CINEMAS_QUERY_FAILED +
                                        " Girilen şehir: " + city + ", özel salon: " + specialHall
                        ));
            } catch (Exception e) {
                throw new ResourceNotFoundException(
                        ErrorMessages.CINEMAS_QUERY_FAILED + " Detay: " + e.getMessage()
                );
            }
        } else {
            // Tüm sinemaları al, liste boşsa exception
            cinemas = cinemaRepository.findAll();
            if (cinemas.isEmpty()) {
                throw new ResourceNotFoundException(
                        ErrorMessages.CINEMA_NOT_FOUND + " Veritabanında kayıtlı sinema yok."
                );
            }
        }
        // DTO dönüşümü: sinema + özel salon bilgileri
        try {
            return cinemas.stream()
                    .map(cinemaMapper::convertCinemaAndHallToResponse)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException(ErrorMessages.CINEMA_HALLS_FAILED + " Detay: " + e.getMessage());
        }
    }


    public CinemaResponse createCinema(CinemaRequest request) {
        City city = cityRepository.findByNameIgnoreCase(request.getCityName())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.CITY_NOT_FOUND, request.getCityName())
                ));

        Cinema cinema = cinemaMapper.convertRequestToCinema(request);
        cinema.setCity(city);

        Cinema savedCinema = cinemaRepository.save(cinema);

        return cinemaMapper.convertCinemaToResponse(savedCinema);
    }

    // Şube kapatma (silme) ve silineni döndürme
    public Cinema deleteCinema(Long cityId, Long cinemaId) {
        // Şehir var mı?
        if (!cityRepository.existsById(cityId)) {
            throw new EntityNotFoundException("City not found with id: " + cityId);
        }

        // Sinema var mı ve o şehre mi ait?
        Cinema cinema = cinemaRepository.findById(cinemaId)
                .orElseThrow(() -> new EntityNotFoundException("Cinema not found with id: " + cinemaId));

        if (!cinema.getCity().getId().equals(cityId)) {
            throw new EntityNotFoundException("Cinema with id " + cinemaId + " does not belong to city " + cityId);
        }

        // Önce nesneyi sakla, sonra sil
        cinemaRepository.delete(cinema);

        return cinema; // Silinen nesne dönülüyor
    }
}
