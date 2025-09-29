package com.cinemax.repository.spec;

import com.cinemax.entity.concretes.business.Movie;
import org.springframework.data.jpa.domain.Specification;

public  final class MovieSpecifications {

    private MovieSpecifications() {}

    /** q: movie.name VE/VEYA movie.description içinde (case-insensitive) arama */
    public static Specification<Movie> nameOrDescriptionContains(String q) {
        return (root, query, cb) -> {
            if (q == null || q.trim().isEmpty()) return cb.conjunction();
            String like = "%" + q.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), like),
                    cb.like(cb.lower(root.get("description")), like)
            );
        };
    }
}
//Not: Eğer entity alanları title/summary ise yukarıdaki iki satırı title/summary olarak değiştirmen yeterli.
