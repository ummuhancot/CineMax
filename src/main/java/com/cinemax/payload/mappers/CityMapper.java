package com.cinemax.payload.mappers;

import com.cinemax.entity.concretes.business.Cinema;
import com.cinemax.entity.concretes.business.City;
import com.cinemax.payload.request.business.CityRequest;
import com.cinemax.payload.response.business.CinemaResponse;
import com.cinemax.payload.response.business.CityResponse;
import com.cinemax.payload.response.business.CityWithCinemasResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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
        return CityResponse.builder()
                .id(city.getId())
                .name(city.getName())
                .build();
    }

    // City (Entity) -> CityWithCinemasResponse (DTO with cinemas)
    public CityWithCinemasResponse mapToCityWithCinemasResponse(City city) {
        return CityWithCinemasResponse.builder()
                .id(city.getId())
                .name(city.getName())
                .cinemas(mapToCinemaResponseList(city.getCinemas()))
                .build();
    }

    // Cinema list -> CinemaResponse list
    public List<CinemaResponse> mapToCinemaResponseList(List<Cinema> cinemas) {
        return cinemas.stream()
                .map(cinema -> CinemaResponse.builder()
                        .id(cinema.getId())
                        .name(cinema.getName())
                        .address(cinema.getAddress())
                        .build())
                .collect(Collectors.toList());
    }

    // Entity güncelleme (Update)
    public void updateEntityFromRequest(City city, CityRequest request) {
        city.setName(request.getName());
    }
}

