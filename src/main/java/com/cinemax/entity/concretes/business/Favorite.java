package com.cinemax.entity.concretes.business;

import com.cinemax.entity.concretes.user.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

    @Entity
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Table(name = "favorites")
    public class Favorite {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @JoinColumn(name = "user_id", nullable = false)
        private User user;

        @JoinColumn(name = "movie_id", nullable = false)
        private Movie movies;

        @JoinColumn(name = "cinema_id", nullable = false)
        private Cinema cinema;


        @Column(nullable = false)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
        private LocalDateTime createdAt;

        @Column(nullable = false)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
        private LocalDateTime updatedAt;

        //service katmanlarında kontrol etmekten daha clean bir yol
        @PrePersist
        protected void onCreate() {
            this.createdAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
        }

        @PreUpdate
        protected void onUpdate() {
            this.updatedAt = LocalDateTime.now();
        }

        /**
         FAVORITE
         Field	Type	Properties
         id	Long(PK)	Auto increment
         user	User	Not null
         movie	Movie	Not null
         cinema	Cinema	Not null
         createdAt	LocalDateTime	Not null, dd-mm-yyyy
         updatedAt	LocalDateTime	Not null, dd-mm-yyyy

         */
}
