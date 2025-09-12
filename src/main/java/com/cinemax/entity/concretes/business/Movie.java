package com.cinemax.entity.concretes.business;

import com.cinemax.entity.enums.MovieStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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

    @Column(nullable = false, length = 100)
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Column(nullable = false, unique = true, length = 20)
    @Size(min = 5, max = 20, message = "Slug must be between 5 and 20 characters")
    private String slug;

    @Column(nullable = false, length = 300)
    @Size(min = 3, max = 300, message = "Summary must be between 3 and 300 characters")
    private String summary;

    @Column(nullable = false)
    private LocalDate releaseDate;

    @Column(nullable = false)
    private Integer duration; // in minutes

    private Double rating; // Nullable

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private String specialHalls; // Nullable

    @Column(nullable = false)
    private String director;

    @ElementCollection
    @CollectionTable(name = "movie_cast", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "actor", nullable = false)
    private List<String> cast;

    @ElementCollection
    @CollectionTable(name = "movie_formats", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "format", nullable = false)
    private List<String> formats;

    @Column(nullable = false)
    private String genre; // Tek genre seçimi bu kullanımda cokluya izin vermez

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "poster_id", referencedColumnName = "id", nullable = false)
    private Image poster;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id", referencedColumnName = "id", nullable = false)
    private Image image;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MovieStatus status = MovieStatus.COMING_SOON;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorite> favorites;

    @ManyToMany
    @JoinTable(
            name = "movie_halls",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "hall_id")
    )
    private List<Hall> halls;
}
