package com.cinemax.service.bussines;

import com.cinemax.entity.concretes.business.Cinema;
import com.cinemax.entity.concretes.business.City;
import com.cinemax.exception.ConflictException;
import com.cinemax.exception.ResourceAlreadyExistsException;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.mappers.CinemaMapper;
import com.cinemax.payload.messages.ErrorMessages;
import com.cinemax.payload.request.business.CinemaRequest;
import com.cinemax.payload.response.business.CinemaHallResponse;
import com.cinemax.payload.response.business.CinemaResponse;
import com.cinemax.repository.businnes.CinemaRepository;
import com.cinemax.repository.businnes.CityRepository;
import com.cinemax.service.validator.UniquePropertyCinemaValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.cinemax.payload.mappers.MovieMapper.generateSlug;

@Service
@RequiredArgsConstructor
public class CinemaService {

    private final CinemaRepository cinemaRepository;
    private final CinemaMapper cinemaMapper;
    private final CityRepository cityRepository;
    private final UniquePropertyCinemaValidator uniquePropertyCinemaValidator;

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
        // Şehir kontrolü
        City city = cityRepository.findByNameIgnoreCase(request.getCityName())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.CITY_NOT_FOUND, request.getCityName())
                ));

        // Email ve phone number benzersizlik kontrolü
        uniquePropertyCinemaValidator.validateUniqueEmailAndPhone(
                request.getEmail(),
                request.getPhoneNumber()
        );

        // Slug oluştur
        String slug = cinemaMapper.generateSlug(request.getName(), request.getCityName());

        // Slug benzersizlik kontrolü
        if (cinemaRepository.existsBySlug(slug)) {
            throw new ConflictException(
                    String.format(ErrorMessages.CINEMA_ALREADY_EXISTS_WITH_SLUG, slug)
            );
        }

        // Request → Entity mapping
        Cinema cinema = cinemaMapper.convertRequestToCinema(request);
        cinema.setSlug(slug);
        cinema.setCity(city);

        // Kaydet
        Cinema savedCinema = cinemaRepository.save(cinema);

        // Entity → Response mapping
        return cinemaMapper.convertCinemaToResponse(savedCinema);
    }


    // Şube kapatma (silme) ve silineni döndürme
    public Cinema deleteCinema(Long cityId, Long cinemaId) {
        // Şehir var mı?
        if (!cityRepository.existsById(cityId)) {
            throw new EntityNotFoundException(String.format(ErrorMessages.CITY_NOT_FOUND));
        }

        // Sinema var mı ve o şehre mi ait?
        Cinema cinema = cinemaRepository.findById(cinemaId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.CINEMA_NOT_FOUND));

        if (!cinema.getCity().getId().equals(cityId)) {
            throw new EntityNotFoundException(ErrorMessages.CINEMA_NOT_IN_CITY);
        }

        // Önce nesneyi sakla, sonra sil
        cinemaRepository.delete(cinema);

        return cinema; // Silinen nesne dönülüyor
    }

    @Transactional
    public CinemaResponse updateCinema(Long cinemaId, CinemaRequest request) {
        // 1. Mevcut Cinema'yı bul
        Cinema cinema = cinemaRepository.findById(cinemaId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.CINEMA_NOT_FOUND, cinemaId)
                ));

        // 2. City kontrolü
        City city = cityRepository.findByNameIgnoreCase(request.getCityName())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.CITY_NOT_FOUND, request.getCityName())
                ));

        // 3. Email ve telefon benzersizliğini kontrol et
        if (!cinema.getEmail().equalsIgnoreCase(request.getEmail())) {
            uniquePropertyCinemaValidator.validateUniqueEmailAndPhone(request.getEmail(), null);
        }
        if (!cinema.getPhoneNumber().equals(request.getPhoneNumber())) {
            uniquePropertyCinemaValidator.validateUniqueEmailAndPhone(null, request.getPhoneNumber());
        }

        // 4. Slug benzersizliği kontrolü
        String newSlug = cinemaMapper.generateSlug(request.getName(), request.getCityName());
        if (!cinema.getSlug().equals(newSlug) && cinemaRepository.existsBySlug(newSlug)) {
            throw new ResourceAlreadyExistsException(
                    String.format(ErrorMessages.CINEMA_SLUG_EXISTS, newSlug)
            );
        }

        // 5. Cinema entity'sini güncelle
        cinemaMapper.updateCinemaFields(cinema, request, city, newSlug);

        // 6. Kaydet
        Cinema updatedCinema = cinemaRepository.save(cinema);

        // 7. Response oluştur
        return cinemaMapper.convertCinemaToResponse(updatedCinema);
    }
}
