package com.cinemax.payload.mappers;

import com.cinemax.entity.concretes.business.City;
import com.cinemax.payload.request.business.CityRequest;
import com.cinemax.payload.response.business.CityResponse;
import org.springframework.stereotype.Component;

@Component
public class CityMapper {
    // CityRequest -> City (Entity)
    public City mapRequestToEntity(CityRequest cityRequest) {
        return City.builder()
                .name(cityRequest.getName())
                .build();
    }

    // City (Entity) -> CityResponse (DTO)
    public CityResponse mapEntityToResponse(City city) {
        CityResponse cityResponse = new CityResponse();
        cityResponse.setId(city.getId());
        cityResponse.setName(city.getName());
        return cityResponse;
    }
}
