package com.cinemax.service.helper;

import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.entity.concretes.user.User;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.messages.ErrorMessages;
import com.cinemax.repository.businnes.MovieRepository;
import com.cinemax.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FavoriteHelper {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    /**
     * Email'e göre kullanıcıyı bulur. Bulamazsa ResourceNotFoundException fırlatır.
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.USER_NOT_FOUND, email)
                ));
    }

    /**
     * İsteğe bağlı: ID'ye göre film bulmak için ek metod
     */
    public Movie getMovieById(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.MOVIE_NOT_FOUND, movieId)
                ));
    }
}
