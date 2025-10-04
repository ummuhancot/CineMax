package com.cinemax.service.bussines;

import com.cinemax.entity.concretes.business.City;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.mappers.CityMapper;
import com.cinemax.payload.request.business.CityRequest;
import com.cinemax.payload.response.business.CityResponse;
import com.cinemax.payload.response.business.CityWithCinemasResponse;
import com.cinemax.repository.businnes.CityRepository;
import com.cinemax.service.helper.CityHelper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;
    private final CityMapper cityMapper;
    private final CityHelper cityHelper;


    public CityResponse saveCity(CityRequest request) {
        cityHelper.validateCityRequest(request);
        // Request → Entity
        City city = cityMapper.mapRequestToEntity(request);
        // Repository save
        City savedCity = cityRepository.save(city);
        // Entity → Response
        return cityMapper.mapEntityToResponse(savedCity);
    }

    public CityWithCinemasResponse getCityWithCinemas(Long cityId) {
        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new ResourceNotFoundException("City not found with id: " + cityId));

        return CityWithCinemasResponse.builder()
                .id(city.getId())
                .name(city.getName())
                .cinemas(cityMapper.mapToCinemaResponseList(city.getCinemas())) // burası artık DTO listesi döndürüyor
                .build();
    }

    // Şehir kapatma (silme) ve silineni döndürme
    public City deleteCity(Long cityId) {
        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new EntityNotFoundException("City not found with id: " + cityId));

        // önce nesneyi sakla
        City deletedCity = city;

        // sonra sil
        cityRepository.delete(city);

        return deletedCity;
    }


    @Transactional
    public CityResponse updateCity(Long cityId, CityRequest request) {
        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new ResourceNotFoundException("City with id " + cityId + " not found"));

        // Mapper kullanarak entity güncellemesi
        cityMapper.updateEntityFromRequest(city, request);

        // Repo save
        City updatedCity = cityRepository.save(city);

        // Response’a map et (cinema listesi dahil)
        return cityMapper.mapEntityToResponse(updatedCity);
    }
}
