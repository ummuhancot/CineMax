package com.cinemax.service.bussines;

import com.cinemax.entity.concretes.business.Hall;
import com.cinemax.entity.concretes.business.Image;
import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.entity.enums.HallType;
import com.cinemax.entity.enums.MovieStatus;
import com.cinemax.exception.BadRequestException;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.mappers.MovieAdminMapper;
import com.cinemax.payload.mappers.MovieMapper;
import com.cinemax.payload.mappers.MovieShowTimesMapper;
import com.cinemax.payload.mappers.ShowTimeMapper;
import com.cinemax.payload.messages.ErrorMessages;
import com.cinemax.payload.request.business.MovieRequest;
import com.cinemax.payload.response.business.MovieAdminResponse;
import com.cinemax.payload.response.business.MovieResponse;
import com.cinemax.payload.response.business.MovieShowTimesResponse;
import com.cinemax.repository.businnes.HallRepository;
import com.cinemax.repository.businnes.ImageRepository;
import com.cinemax.repository.businnes.MovieRepository;
import com.cinemax.repository.businnes.ShowTimeRepository;
import com.cinemax.service.helper.MovieHelper;
import com.cinemax.service.validator.MovieValidator;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final ImageRepository imageRepository;
    private final ShowTimeRepository showTimeRepository;
    private final MovieMapper movieMapper;
    private final MovieShowTimesMapper movieShowTimesMapper;
    private final MovieHelper movieHelper;
    private final HallRepository hallRepository;
    private final ShowTimeMapper showTimeMapper;
    private final MovieAdminMapper movieAdminMapper;
    private final ShowTimeService showTimeService;
    private final MovieValidator movieValidator;

    @Transactional
    public MovieResponse saveMovie(MovieRequest request) {
        // 1️⃣ Salonları al
        List<Hall> halls = movieHelper.getHallsOrThrow(request.getHallIds());

        // 2️⃣ Benzersiz slug üret (mapper içinde kullanılacak)
        String slug = MovieValidator.generateUniqueSlug(request, halls, movieRepository);

        // 3️⃣ Salon bazlı kontrol (aynı film aynı salonda varsa hata fırlat)
        for (Hall hall : halls) {
            movieValidator.validateUniqueMovieInHalls(slug, hall);
        }

        // 4️⃣ Movie oluştur (mapper durationDays ve slug'ı set ediyor)
        Movie movie = movieMapper.mapMovieRequestToMovie(request, halls, slug);

        // 6️⃣ Kaydet
        movieRepository.save(movie);

        List<Image> images = imageRepository.findByMovieId(movie.getId());

        // 7️⃣ Cevap dön
        return movieMapper.mapMovieToMovieResponse(movie,images);
    }

    public MovieResponse updateMovie(Long movieId, MovieRequest request) {
        // Mevcut movie al
        Movie movie = movieHelper.getMovieOrThrow(movieId);

        // Hall listesi
        List<Hall> halls = movieHelper.getHallsOrThrow(request.getHallIds());

        // Poster opsiyonel
        Image poster = null;
        if (request.getPosterId() != null) {
            poster = movieHelper.getPosterOrThrow(request.getPosterId());
        }

        // Movie güncelle
        movieMapper.updateMovieFromRequest(movie, request, halls, poster);

        // Kaydet
        movieRepository.save(movie);

        List<Image> images = imageRepository.findByMovieId(movie.getId());

        return movieMapper.mapMovieToMovieResponse(movie,images);
    }

    @Transactional
    public MovieResponse deleteById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.MOVIE_DELETE_FAILED + id));

        List<Image> images = imageRepository.findByMovieId(movie.getId());
        MovieResponse response = movieMapper.mapMovieToMovieResponse(movie,images);

        // Movie’yi sil
        movieRepository.delete(movie);
        movieRepository.flush(); // commit’i garantiye alır

        return response;
    }


    public MovieShowTimesResponse getUpcomingShowTimes(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie with id " + movieId + " not found"));
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        var showTimes = showTimeRepository.findUpcomingShowTimes(movieId, today, now);
        return movieShowTimesMapper.mapMovieWithShowTimesToResponse(movie, showTimes);
    }

    public List<MovieResponse> getMoviesByHallType(String hallTypeStr, int page, int size, String sort, String type) {
        // 🔹 Enum dönüşümü
        HallType hallType;
        try {
            hallType = HallType.valueOf(hallTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Geçersiz salon tipi: " + hallTypeStr);
        }

        // 🔹 Pageable oluştur
        Sort.Direction direction = type.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));

        // 🔹 Veritabanından doğrudan sayfalanmış şekilde çek
        Page<Movie> pagedMovies = movieRepository.findDistinctByHallType(hallType, pageable);

        // 🔹 Movie → Response map
        return pagedMovies.stream()
                .map(movie -> {
                    List<Image> images = imageRepository.findByMovieId(movie.getId());
                    MovieResponse response = movieMapper.mapMovieToMovieResponse(movie, images);

                    // 🔹 İlgili salonlardan birini göster (örnek olarak ilkini alıyoruz)
                    movie.getHalls().stream()
                            .filter(h -> h.getType() == hallType)
                            .findFirst()
                            .ifPresent(hall -> response.setHalls(List.of(hall.getName())));

                    return response;
                })
                .toList();
    }

    public Page<MovieResponse> getMoviesInTheaters(Pageable pageable) {
        return movieRepository.findByStatus(MovieStatus.IN_THEATERS, pageable)
                .map(movie -> {
                    List<Image> images = imageRepository.findByMovieId(movie.getId());
                    return movieMapper.mapMovieToMovieResponse(movie, images);
                });
    }

    public Page<MovieResponse> getMoviesInTheatersWithDateCheck(Pageable pageable) {
        LocalDate today = LocalDate.now();
        return movieRepository.findByStatusAndReleaseDateBefore(MovieStatus.IN_THEATERS, today, pageable)
                .map(movie -> {
                    List<Image> images = imageRepository.findByMovieId(movie.getId());
                    return movieMapper.mapMovieToMovieResponse(movie, images);
                });
    }

    /**
     * T-5: Yakında (COMING_SOON) olan ya da vizyonda olmayan ve çıkış tarihi gelecekte olan filmler
     */
    public List<MovieResponse> getComingSoon(Integer page, Integer size, String sort, String type) {
        int p = page == null || page < 0 ? 0 : page;
        int s = size == null || size <= 0 ? 10 : size;
        String sortField = (sort == null || sort.isBlank()) ? "releaseDate" : sort;
        Sort.Direction dir = "desc".equalsIgnoreCase(type) ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(p, s, Sort.by(dir, sortField));
        try {
            Page<Movie> pageResult = movieRepository.findByStatus(MovieStatus.COMING_SOON, pageable);
            return pageResult.stream().map(movie -> {
                List<Image> images = imageRepository.findByMovieId(movie.getId());
                return movieMapper.mapMovieToMovieResponse(movie, images);
            }).toList();
        } catch (Throwable ignore) {
            LocalDate today = LocalDate.now();
            var all = movieRepository.findAll(Sort.by(dir, sortField));
            var filtered = all.stream()
                    .filter(m -> {
                        MovieStatus st = m.getStatus();
                        if (st == MovieStatus.COMING_SOON) return true;
                        if (st != MovieStatus.IN_THEATERS) {
                            LocalDate rd = m.getReleaseDate();
                            return rd != null && rd.isAfter(today);
                        }
                        return false;
                    })
                    .toList();

            int from = Math.min(p * s, filtered.size());
            int to = Math.min(from + s, filtered.size());
            return filtered.subList(from, to).stream()
                    .map(movie -> {
                        List<Image> images = imageRepository.findByMovieId(movie.getId());
                        return movieMapper.mapMovieToMovieResponse(movie, images);
                    })
                    .toList();
        }
    }

    // --------- EKLENEN METOT (T-6) ---------
    @Transactional(readOnly = true)
    public List<MovieResponse> searchMovies(String q,
                                            Integer page,
                                            Integer size,
                                            String sort,
                                            String type) {

        int p = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size <= 0) ? 10 : size;
        String sortField = (sort == null || sort.isBlank()) ? "title" : sort;
        Sort.Direction dir = "desc".equalsIgnoreCase(type) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(p, s, Sort.by(dir, sortField));

        // 1) ÖNCE: repository'de "search" varsa onu kullan
        try {
            Page<Movie> pageResult = movieRepository.search(q, pageable);
            return pageResult.stream()
                    .map(movie -> {
                        List<Image> images = imageRepository.findByMovieId(movie.getId());
                        return movieMapper.mapMovieToMovieResponse(movie, images);
                    })
                    .toList();
        } catch (Throwable ignore) {
            // 2) FALLBACK: findAll + filtre + manuel sayfalama
            var all = movieRepository.findAll(Sort.by(dir, sortField));

            String needle = (q == null) ? "" : q.trim().toLowerCase();
            var filtered = all.stream().filter(m -> {
                try {
                    Method gt = m.getClass().getMethod("getTitle");
                    Method gd = null;
                    try { gd = m.getClass().getMethod("getDescription"); } catch (Exception ignore2) {}

                    String title = String.valueOf(gt.invoke(m)).toLowerCase();
                    String desc  = gd == null ? "" : String.valueOf(gd.invoke(m)).toLowerCase();

                    return needle.isEmpty() || title.contains(needle) || desc.contains(needle);
                } catch (Exception e) {
                    return needle.isEmpty();
                }
            }).toList();

            int from = Math.min(p * s, filtered.size());
            int to   = Math.min(from + s, filtered.size());
            var slice = (from <= to) ? filtered.subList(from, to) : new ArrayList<Movie>();

            return slice.stream()
                    .map(movie -> {
                        List<Image> images = imageRepository.findByMovieId(movie.getId());
                        return movieMapper.mapMovieToMovieResponse(movie, images);
                    })
                    .toList();
        }
    }

    @Transactional(readOnly = true)
    public MovieResponse getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> movieHelper.movieNotFound(id));
        List<Image> images = imageRepository.findByMovieId(movie.getId());
        return movieMapper.mapMovieToMovieResponse(movie, images);
    }


    @Transactional(readOnly = true)
    public MovieAdminResponse getMovieByIdAdmin(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() ->movieHelper.movieNotFound(id));
        return movieAdminMapper.toAdminResponse(movie);
    }

    @Transactional(readOnly = true)
    public Page<MovieResponse> getAllMovies(int page, int size, String sort, String type) {
        // 🔹 Sıralama yönü belirle
        Sort.Direction direction = type.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));

        // 🔹 Veritabanından sayfalı olarak filmleri getir
        Page<Movie> moviePage = movieRepository.findAll(pageable);

        // 🔹 Movie → MovieResponse dönüşümü
        return moviePage.map(movie -> {
            List<Image> images = imageRepository.findByMovieId(movie.getId());
            return movieMapper.mapMovieToMovieResponse(movie, images);
        });
    }


    @Transactional
    public List<MovieResponse> saveMovies(List<MovieRequest> requests) {
        List<MovieResponse> responses = new ArrayList<>();

        for (MovieRequest request : requests) {

            if (request.getHallIds() == null || request.getHallIds().isEmpty()) {
                throw new BadRequestException("Movie must have at least one hall assigned.");
            }

            // 1️⃣ Hall listesi
            List<Hall> halls = movieHelper.getHallsOrThrow(request.getHallIds());

            // 2️⃣ Benzersiz slug üret
            String slug = MovieValidator.generateUniqueSlug(request, halls, movieRepository);

            // 3️⃣ Salon bazlı kontrol (aynı film aynı salonda varsa hata fırlat)
            for (Hall hall : halls) {
                movieValidator.validateUniqueMovieInHalls(slug, hall);
            }

            // 4️⃣ Movie oluşturma (mapper durationDays ve slug'ı set ediyor)
            Movie movie = movieMapper.mapMovieRequestToMovie(request, halls, slug);

            // 5️⃣ Başlangıç durumu → IN_THEATERS
            movie.setStatus(MovieStatus.IN_THEATERS);

            // 6️⃣ Movie kaydet
            movieRepository.save(movie);

            // 7️⃣ Response ekle
            List<Image> images = imageRepository.findByMovieId(movie.getId());
            responses.add(movieMapper.mapMovieToMovieResponse(movie,images));
        }

        return responses;
    }


}