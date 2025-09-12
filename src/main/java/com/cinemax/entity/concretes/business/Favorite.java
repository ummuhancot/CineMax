package com.cinemax.entity.concretes.business;

import com.cinemax.entity.concretes.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "user_id", nullable = false)
        private User user;


	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "movie_id", nullable = false)
        private Movie movie;

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "cinema_id", nullable = false)
        private Cinema cinema;

        @CreationTimestamp
        @Column(nullable = false)
        private LocalDateTime createdAt;

        @UpdateTimestamp
        private LocalDateTime updatedAt;

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
