package com.cinemax.repository.businnes;

import com.cinemax.entity.concretes.business.Cinema;
import com.cinemax.entity.concretes.business.Favorite;
import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.entity.concretes.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {


    List<Favorite> findAllByUser(User user);

    boolean existsByUserAndMovieAndCinema(User user, Movie movie, Cinema cinema);

    Optional<Favorite> findByUserAndMovieAndCinema(User user, Movie movie, Cinema cinema);

}
