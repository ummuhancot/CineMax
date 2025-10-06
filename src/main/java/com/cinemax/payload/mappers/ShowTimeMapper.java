package com.cinemax.payload.mappers;

import com.cinemax.entity.concretes.business.Hall;
import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.entity.concretes.business.ShowTime;
import com.cinemax.payload.request.business.ShowTimeRequest;
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
    // Request'ten ShowTime entity oluştur
    public ShowTime toEntity(ShowTimeRequest request, Movie movie, Hall hall) {
        return ShowTime.builder()
                .date(request.getDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .movie(movie)
                .hall(hall)
                .build();
    }

    // Entity'den Response oluştur
    public ShowTimeResponse toResponse(ShowTime showTime) {
        if (showTime == null) return null;

        return ShowTimeResponse.builder()
                .id(showTime.getId())
                .startDateTime(showTime.getDate() != null && showTime.getStartTime() != null
                        ? LocalDateTime.of(showTime.getDate(), showTime.getStartTime()) : null)
                .endDateTime(showTime.getDate() != null && showTime.getEndTime() != null
                        ? LocalDateTime.of(showTime.getDate(), showTime.getEndTime()) : null)
                .hallName(showTime.getHall() != null ? showTime.getHall().getName() : null)
                .movieId(showTime.getMovie() != null ? showTime.getMovie().getId() : null)
                .movieTitle(showTime.getMovie() != null ? showTime.getMovie().getTitle() : null)
                .build();
    }
}
