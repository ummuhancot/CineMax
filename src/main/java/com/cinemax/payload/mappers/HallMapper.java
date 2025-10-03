package com.cinemax.payload.mappers;

import com.cinemax.entity.concretes.business.Cinema;
import com.cinemax.entity.concretes.business.Hall;
import com.cinemax.payload.request.business.HallRequest;
import com.cinemax.payload.response.business.CinemaResponse;
import com.cinemax.payload.response.business.HallResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class HallMapper {

    // Request DTO → Entity
    public Hall convertRequestToHall(HallRequest request, Cinema cinema) {
        return Hall.builder()
                .name(request.getName())
                .seatCapacity(request.getSeatCapacity())
                .isSpecial(request.getIsSpecial())
                .type(request.getType())
                .cinema(cinema)
                .build();
    }

    // Entity → Response DTO
    public HallResponse convertHallToResponse(Hall hall) {
        return HallResponse.builder()
                .id(hall.getId())
                .name(hall.getName())
                .seatCapacity(hall.getSeatCapacity())
                .isSpecial(hall.getIsSpecial())
                .type(hall.getType())
                .cinemaName(hall.getCinema().getName())
                .build();
    }
}
