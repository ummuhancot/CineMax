package com.cinemax.repository.businnes;

import com.cinemax.entity.concretes.business.Favorite;
import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.entity.concretes.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByUserAndMovie(User user, Movie movie);

    List<Favorite> findAllByUser(User user);

    void deleteByUserAndMovie(User user, Movie movie);
}
