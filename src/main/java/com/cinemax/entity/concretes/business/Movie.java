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

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
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
    @CollectionTable(
			    name = "movie_cast",
			    joinColumns = @JoinColumn(name = "movie_id"),
			    foreignKey = @ForeignKey(name = "FK_MOVIE_CAST")
    )
    @Column(name = "cast_member", nullable = false)
    private List<String> cast;

    @ElementCollection
    @CollectionTable(
			    name = "movie_formats",
			    joinColumns = @JoinColumn(name = "movie_id"),
			    foreignKey = @ForeignKey(name = "FK_MOVIE_FORMATS")
    )
    @Column(name = "format_name", nullable = false)
    private List<String> formats;

    @Column(nullable = false)
    private String genre; // Tek genre seçimi bu kullanımda cokluya izin vermez

    @OneToOne(cascade = CascadeType.ALL, fetch =  FetchType.LAZY, optional = false, orphanRemoval = true)
    @JoinColumn(name = "poster_id", nullable = false, unique = true)
    private Image poster;

    //image icin eklendi
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
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

	@OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ShowTime> showTimes;
}