package com.cinemax.payload.mappers;

import com.cinemax.entity.concretes.business.Cinema;
import com.cinemax.entity.concretes.business.Hall;
import com.cinemax.entity.enums.HallType;
import com.cinemax.payload.request.business.HallRequest;
import com.cinemax.payload.response.business.HallResponse;
import org.springframework.stereotype.Component;

@Component
public class HallMapper {

    // Request DTO → Entity
    public Hall convertRequestToHall(HallRequest request, Cinema cinema) {
        HallType hallType = null;

        if (request.getType() != null && !request.getType().isBlank()) {
            try {
                hallType = HallType.valueOf(request.getType().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid hall type: " + request.getType());
            }
        }

        return Hall.builder()
                .name(request.getName())
                .seatCapacity(request.getSeatCapacity())
                .isSpecial(request.getIsSpecial())
                .type(hallType) // artık enum olarak set ediliyor
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
                .type(hall.getType() != null ? hall.getType().getLabel() : null) // enum label kullanıyoruz
                .cinemaName(hall.getCinema() != null ? hall.getCinema().getName() : null)
                .build();
    }


    public HallResponse mapHallToResponse(Hall hall) {
        return HallResponse.builder()
                .id(hall.getId())
                .name(hall.getName())
                .seatCapacity(hall.getSeatCapacity())
                .isSpecial(hall.getType() == HallType.VIP || hall.getType() == HallType.THREE_D)
                .type(hall.getType() != null ? hall.getType().getLabel() : null) // enum label kullanıyoruz
                .cinemaName(hall.getCinema() != null ? hall.getCinema().getName() : null)
                .build();
    }

    @SuppressWarnings("unused")
    public Hall updateHallFromRequest(Hall hall, HallRequest request, Cinema cinema) {
        HallType hallType = null;

        if (request.getType() != null && !request.getType().isBlank()) {
            try {
                hallType = HallType.valueOf(request.getType().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid hall type: " + request.getType());
            }
        }

        hall.setName(request.getName());
        hall.setSeatCapacity(request.getSeatCapacity());
        hall.setIsSpecial(request.getIsSpecial());
        hall.setType(hallType);
        hall.setCinema(cinema);

        return hall;
    }




}
