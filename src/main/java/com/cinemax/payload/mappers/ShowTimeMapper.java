package com.cinemax.payload.mappers;

import com.cinemax.entity.concretes.business.Hall;
import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.entity.concretes.business.ShowTime;
import com.cinemax.payload.request.business.ShowTimeRequest;
import com.cinemax.payload.response.business.ShowTimeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShowTimeMapper {

    public ShowTimeResponse mapShowTimeToResponse(ShowTime showTime) {
        if (showTime == null) return null;

        return ShowTimeResponse.builder()
                .id(showTime.getId())
                .date(showTime.getDate())
                .startDateTime(showTime.getStartTime())
                .endDateTime(showTime.getEndTime())
                .hallName(showTime.getHall() != null ? showTime.getHall().getName() : null)
                .movieId(showTime.getMovie() != null ? showTime.getMovie().getId() : null)
                .movieTitle(showTime.getMovie() != null ? showTime.getMovie().getTitle() : null)
                .build();
    }

    /**
     * Request → Entity
     */
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
        return mapShowTimeToResponse(showTime);
    }


    /**
     * Var olan ShowTime entity’sini request verisine göre günceller.
     */
    public void updateEntityFromRequest(ShowTime existing, ShowTimeRequest request, Movie movie, Hall hall) {
        if (existing == null || request == null) return;

        existing.setDate(request.getDate());
        existing.setStartTime(request.getStartTime());
        existing.setEndTime(request.getEndTime());
        existing.setMovie(movie);
        existing.setHall(hall);
    }
}
