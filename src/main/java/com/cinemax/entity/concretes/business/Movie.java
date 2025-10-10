package com.cinemax.entity.concretes.business;

import com.cinemax.entity.enums.MovieStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(unique = true)
    private String slug;

    @Column(nullable = false)
    private String summary;

    @Column(nullable = false)
    private LocalDate releaseDate;

    @Column(nullable = false)
    private Integer duration; // dakika cinsinden

    private Double rating; // Nullable

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private String specialHalls; // Opsiyonel

    @Column(nullable = false)
    private String director;

    // Oyuncular listesi
    @ElementCollection
    @CollectionTable(
            name = "movie_cast",
            joinColumns = @JoinColumn(name = "movie_id"),
            foreignKey = @ForeignKey(name = "FK_MOVIE_CAST")
    )
    @Column(name = "cast_member", nullable = false)
    private List<String> cast;

    //  Gösterim formatları (2D, 3D, IMAX vs.)
    @ElementCollection
    @CollectionTable(
            name = "movie_formats",
            joinColumns = @JoinColumn(name = "movie_id"),
            foreignKey = @ForeignKey(name = "FK_MOVIE_FORMATS")
    )
    @Column(name = "format_name", nullable = false)
    private List<String> formats;

    @Column(nullable = false)
    private String genre;

    // Poster ilişkisi (1:1)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poster_id", unique = true)
    private Image poster;

    // Galeri görselleri (1:N)
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovieStatus status = MovieStatus.COMING_SOON;

    // Bilet ilişkisi
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets;

    // Favoriler
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorite> favorites;

    // Salonlar
    @ManyToMany
    @JoinTable(
            name = "movie_halls",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "hall_id")
    )
    private List<Hall> halls;

    // Seanslar
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShowTime> showTimes;
}
