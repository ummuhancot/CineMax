package com.cinemax.service.bussines;

import com.cinemax.entity.concretes.business.Favorite;
import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.entity.concretes.user.User;
import com.cinemax.payload.messages.ErrorMessages;
import com.cinemax.payload.messages.SuccessMessages;
import com.cinemax.payload.request.business.MovieFavoriteRequest;
import com.cinemax.payload.response.business.FavoriteMovieResponse;
import com.cinemax.repository.businnes.FavoriteRepository;
import com.cinemax.service.helper.FavoriteHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final FavoriteHelper favoriteHelper;

    @Transactional
    public void addMovieToFavorites(MovieFavoriteRequest request) {
        User user = favoriteHelper.getUserByEmail(request.getEmail());
        Movie movie = favoriteHelper.getMovieById(request.getMovieId());

        boolean alreadyFavorite = favoriteRepository.existsByUserAndMovie(user, movie);
        if (alreadyFavorite) return;

        Favorite favorite = Favorite.builder()
                .user(user)
                .movie(movie)
                .build();

        favoriteRepository.save(favorite);

    }

    @Transactional
    public String removeMovieFromFavorites(MovieFavoriteRequest request) {
        User user = favoriteHelper.getUserByEmail(request.getEmail());
        Movie movie = favoriteHelper.getMovieById(request.getMovieId());

        boolean alreadyFavorite = favoriteRepository.existsByUserAndMovie(user, movie);
        if (!alreadyFavorite) {
            return ErrorMessages.MOVIE_NOT_IN_FAVORITES;
        }

        favoriteRepository.deleteByUserAndMovie(user, movie);
        return SuccessMessages.FAVORITE_MOVIE_REMOVED;
    }


    /**
     * Kullanıcının tüm favori filmlerini getirir.
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<FavoriteMovieResponse> getAllFavorites(String email) {
        User user = favoriteHelper.getUserByEmail(email);

        List<Favorite> favorites = favoriteRepository.findAllByUser(user);

        return favorites.stream()
                .map(fav -> FavoriteMovieResponse.builder()
                        .movieId(fav.getMovie().getId())
                        .movieTitle(fav.getMovie().getTitle())
                        .addedAt(fav.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }



}