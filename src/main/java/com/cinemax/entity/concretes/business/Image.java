package com.cinemax.entity.concretes.business;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;  // Resim dosya adı

    @Column(nullable = false)
    private String type;  // MIME tipi (image/png, image/jpeg)

    private boolean featured; // Öne çıkan resim mi?

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private byte[] data;  // Sıkıştırılmış resim verisi

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Poster olarak kullanılan resim (1:1)
    @OneToOne(mappedBy = "poster", fetch = FetchType.LAZY)
    private Movie posterOfMovie;

    // Film galerisine ait resimler (n:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;
}
