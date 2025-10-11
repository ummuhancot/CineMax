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
    private String name; // Dosya adı

    @Column(nullable = false)
    private String type; // MIME tipi (image/png, image/jpeg)

    private boolean featured; // Öne çıkan görsel mi?

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private byte[] data; // Görsel verisi (sıkıştırılmış)

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // 🎬 Poster olarak kullanıldığı film (1:1)
    @OneToOne(mappedBy = "poster", fetch = FetchType.LAZY)
    private Movie posterOfMovie;

    // 🎞️ Galeri görseli olduğu film (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie; // poster görsellerde null olabilir
}