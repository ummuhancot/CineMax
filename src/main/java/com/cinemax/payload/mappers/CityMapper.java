package com.cinemax.payload.mappers;

import com.cinemax.entity.concretes.business.Cinema;
import com.cinemax.entity.concretes.business.City;
import com.cinemax.payload.request.business.CityRequest;
import com.cinemax.payload.response.business.CinemaResponse;
import com.cinemax.payload.response.business.CityResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CityMapper {
    // CityRequest -> City (Entity)
    public City mapRequestToEntity(CityRequest cityRequest) {
        return City.builder()
                .name(cityRequest.getName())
                .address(cityRequest.getAddress())
                .build();
    }

    // City (Entity) -> CityResponse (DTO)
    public CityResponse mapEntityToResponse(City city) {
        CityResponse cityResponse = new CityResponse();
        cityResponse.setId(city.getId());
        cityResponse.setName(city.getName());
        cityResponse.setAddress(city.getAddress());
        return cityResponse;
    }

    public List<CinemaResponse> mapToCinemaResponseList(List<Cinema> cinemas) {
        return cinemas.stream()
                .map(cinema -> CinemaResponse.builder()
                        .id(cinema.getId())
                        .name(cinema.getName())
                        .address(cinema.getAddress())
                        .build())
                .collect(Collectors.toList());
    }

    // Entity güncelleme (update)
    public void updateEntityFromRequest(City city, CityRequest request) {
        city.setName(request.getName());
        city.setAddress(city.getAddress());
    }
}
