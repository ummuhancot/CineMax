package com.cinemax.payload.mappers;

import com.cinemax.entity.concretes.business.ShowTime;
import com.cinemax.payload.response.business.ShowTimeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ShowTimeMapper {

    public ShowTimeResponse mapShowTimeToResponse(ShowTime showTime) {
        if (showTime == null) return null;

        ShowTimeResponse response = new ShowTimeResponse();
        response.setId(showTime.getId());
        response.setStartDateTime(LocalDateTime.of(showTime.getDate(), showTime.getStartTime()));
        response.setEndDateTime(LocalDateTime.of(showTime.getDate(), showTime.getEndTime()));
        response.setHallName(showTime.getHall() != null ? showTime.getHall().getName() : null);

        return response;
    }
}
