package com.cinemax.entity.concretes.business;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity @Table(name="halls")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Hall {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String name;
    @Column(nullable=false)
    private Integer seatCapacity;
    @Column(nullable=false)
    private Boolean isSpecial = false;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy, HH:mm")
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy, HH:mm")
    private LocalDateTime updatedAt;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "cinema_id", nullable = false)
//    private Cinema cinema;
//
//    // :arrow_right: OneToMany → Showtime
//    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<ShowTime> showtimes;
}