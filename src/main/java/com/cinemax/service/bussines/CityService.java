package com.cinemax.service.bussines;

import com.cinemax.entity.concretes.business.City;
import com.cinemax.payload.mappers.CityMapper;
import com.cinemax.payload.request.business.CityRequest;
import com.cinemax.payload.response.business.CityResponse;
import com.cinemax.repository.businnes.CityRepository;
import com.cinemax.service.helper.CityHelper;
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
}
