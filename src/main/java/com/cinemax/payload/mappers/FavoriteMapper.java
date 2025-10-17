package com.cinemax.payload.mappers;

import com.cinemax.entity.concretes.business.Favorite;
import com.cinemax.payload.response.business.FavoriteResponse;
import org.springframework.stereotype.Component;

@Component
public class FavoriteMapper {


    public FavoriteResponse mapToResponse(Favorite favorite) {
        return FavoriteResponse.builder()
                .userEmail(favorite.getUser().getEmail())
                .cinemaName(favorite.getCinema().getName())
                .movieTitle(favorite.getMovie().getTitle())
                .addedAt(favorite.getCreatedAt())
                .build();
    }
}
