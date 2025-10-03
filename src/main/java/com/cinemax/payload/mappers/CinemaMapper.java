package com.cinemax.payload.mappers;

import com.cinemax.entity.concretes.business.Cinema;
import com.cinemax.entity.concretes.business.Hall;
import com.cinemax.payload.request.business.CinemaRequest;
import com.cinemax.payload.response.business.CinemaHallResponse;
import com.cinemax.payload.response.business.CinemaResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CinemaMapper {


    public CinemaHallResponse convertCinemaAndHallToResponse(Cinema cinema) {
        return CinemaHallResponse.builder()
                .id(cinema.getId())
                .name(cinema.getName())
                .slug(cinema.getSlug())
                .address(cinema.getAddress())
                .phoneNumber(cinema.getPhoneNumber())
                .email(cinema.getEmail())
                .cityName(cinema.getCity() != null ? cinema.getCity().getName() : null)
                .specialHall(getSpecialHallsString(cinema))
                .build();
    }

    // Halls listesindeki isSpecial veya isim ve type bilgilerini birleştirip string yapar
    private String getSpecialHallsString(Cinema cinema) {
        if (cinema.getHalls() == null || cinema.getHalls().isEmpty()) {
            return null;
        }

        return cinema.getHalls()
                .stream()
                .filter(Hall::getIsSpecial) // Özel salonları al
                .map(hall -> hall.getName() + " (" + hall.getType() + ")") // İsim ve type birleştir
                .distinct() // Tekrar edenleri kaldır
                .collect(Collectors.joining(", "));
    }

    // Request → Entity
    public Cinema convertRequestToCinema(CinemaRequest request) {
        return Cinema.builder()
                .name(request.getName())
                .slug(generateSlug(request.getName()))
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .build();
    }

    private String generateSlug(String name) {
        return name.toLowerCase().replaceAll("[^a-z0-9]+", "-");
    }

    // Entity → Response
    public CinemaResponse convertCinemaToResponse(Cinema cinema) {
        return CinemaResponse.builder()
                .id(cinema.getId())
                .name(cinema.getName())
                .slug(cinema.getSlug())
                .address(cinema.getAddress())
                .phoneNumber(cinema.getPhoneNumber())
                .email(cinema.getEmail())
                .cityName(cinema.getCity() != null ? cinema.getCity().getName() : null)
                .build();
    }

}
