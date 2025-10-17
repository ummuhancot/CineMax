package com.cinemax.service.bussines;

import com.cinemax.entity.concretes.business.Favorite;
import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.entity.concretes.user.User;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.exception.UnauthorizedException;
import com.cinemax.payload.mappers.FavoriteMapper;
import com.cinemax.payload.messages.ErrorMessages;
import com.cinemax.payload.messages.SuccessMessages;
import com.cinemax.payload.request.business.FavoriteRequest;
import com.cinemax.payload.response.business.FavoriteResponse;
import com.cinemax.repository.businnes.FavoriteRepository;
import com.cinemax.service.helper.FavoriteHelper;
import com.cinemax.service.validator.FavoriteValidator;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final FavoriteHelper favoriteHelper;
    private final FavoriteMapper favoriteMapper;
    private final FavoriteValidator favoriteValidator;

    @Transactional
    public FavoriteResponse addMovieToFavorites(Long userId, FavoriteRequest request) {

        // 1️⃣ User, Movie ve Cinema objelerini al
        var user = favoriteHelper.getUserById(userId);
        var movie = favoriteHelper.getMovieById(request.getMovieId());
        var cinema = favoriteHelper.getCinemaById(request.getCinemaId());

        // 2️⃣ Validator ile kontrol et aynı favorinin eklenmediğinden emin ol
        favoriteValidator.validateUniqueFavorite(user, movie, cinema);

        // 3️⃣ Favori kaydını oluştur ve kaydet
        Favorite favorite = Favorite.builder()
                .user(user)
                .movie(movie)
                .cinema(cinema)
                .build();

        favoriteRepository.save(favorite);

        // 4️⃣ Response döndür
        return favoriteMapper.mapToResponse(favorite);
    }


    @Transactional
    public FavoriteResponse removeFavorite(Long userId, Long favoriteId) {

        // 1️⃣ User'ı al
        User user = favoriteHelper.getUserById(userId);

        // 2️⃣ Favori kaydını bul
        Favorite favorite = favoriteRepository.findById(favoriteId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.FAVORITE_NOT_FOUND));

        // 3️⃣ Favori gerçekten bu kullanıcıya mı ait kontrol et
        if (!favorite.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException(ErrorMessages.FAVORITE_NOT_OWNED_BY_USER);
        }

        // 4️⃣ Favoriyi sil
        favoriteRepository.delete(favorite);

        // 5️⃣ Silinen favoriyi response olarak dön
        return favoriteMapper.mapToResponse(favorite);
    }


    /**
     * Kullanıcının tüm favori filmlerini getirir.
     */
    @Transactional(readOnly = true)
    public List<FavoriteResponse> getAllFavorites(Long userId) {

        // User'ı ID üzerinden al
        User user = favoriteHelper.getUserById(userId);

        // Kullanıcının tüm favorilerini çek
        List<Favorite> favorites = favoriteRepository.findAllByUser(user);

        // Favorite → FavoriteResponse dönüşümü
        return favorites.stream()
                .map(favoriteMapper::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FavoriteResponse> getAllFavoritesForAllUsers() {

        List<Favorite> favorites = favoriteRepository.findAll();

        // FavoriteResponse’a dönüştür
        return favorites.stream()
                .map(favoriteMapper::mapToResponse)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public FavoriteResponse getFavoriteById(Long userId, Long favoriteId) {

        // 1️⃣ User'ı al
        User user = favoriteHelper.getUserById(userId);

        // 2️⃣ Favori kaydını bul
        Favorite favorite = favoriteRepository.findById(favoriteId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.FAVORITE_NOT_FOUND));

        // 3️⃣ Favori gerçekten bu kullanıcıya mı ait kontrol et
        if (!favorite.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException(ErrorMessages.FAVORITE_NOT_OWNED_BY_USER);
        }

        // 4️⃣ Response oluştur
        return favoriteMapper.mapToResponse(favorite);
    }

}