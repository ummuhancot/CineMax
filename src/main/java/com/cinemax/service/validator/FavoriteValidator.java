package com.cinemax.service.validator;

import com.cinemax.entity.concretes.business.Cinema;
import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.entity.concretes.user.User;
import com.cinemax.exception.ResourceAlreadyExistsException;
import com.cinemax.payload.messages.ErrorMessages;
import com.cinemax.repository.businnes.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FavoriteValidator {

    private final FavoriteRepository favoriteRepository;

    public void validateUniqueFavorite(User user, Movie movie, Cinema cinema) {
        boolean exists = favoriteRepository.existsByUserAndMovieAndCinema(user, movie, cinema);
        if (exists) {
            throw new ResourceAlreadyExistsException(ErrorMessages.FAVORITE_ALREADY_EXISTS);
        }
    }
}
