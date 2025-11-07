package com.cinemax.service.businnes;

import com.cinemax.entity.concretes.business.Image;
import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.entity.enums.MovieStatus;
import com.cinemax.payload.mappers.MovieMapper;
import com.cinemax.payload.mappers.MovieShowTimesMapper;
import com.cinemax.payload.mappers.ShowTimeMapper;
import com.cinemax.payload.response.business.MovieResponse;
import com.cinemax.repository.businnes.HallRepository;
import com.cinemax.repository.businnes.ImageRepository;
import com.cinemax.repository.businnes.MovieRepository;
import com.cinemax.repository.businnes.ShowTimeRepository;
import com.cinemax.service.bussines.MovieService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq; // <-- HATA BURADAYDI, BU SATIR EKLENDİ

@ExtendWith(MockitoExtension.class)
class MovieServiceComingSoonTest {

    @Mock private MovieRepository movieRepository;
    @Mock private HallRepository hallRepository;               // ctor bağımlılıkları için mock
    @Mock private ImageRepository imageRepository;
    @Mock private ShowTimeRepository showTimeRepository;
    @Mock private MovieMapper movieMapper;
    @Mock private ShowTimeMapper showTimeMapper;
    @Mock private MovieShowTimesMapper movieShowTimesMapper;

    @InjectMocks private MovieService movieService;

    @Test
    void returnsOnlyComingSoon() {
        // given
        Movie soon = new Movie();
        soon.setId(1L);
        soon.setTitle("Soon");
        soon.setStatus(MovieStatus.COMING_SOON);
        soon.setReleaseDate(LocalDate.now().plusDays(7));

        Movie now = new Movie(); // Bu film (IN_THEATERS) DAHİL EDİLMEMELİ
        now.setId(2L);
        now.setTitle("Now");
        now.setStatus(MovieStatus.IN_THEATERS);
        now.setReleaseDate(LocalDate.now().minusDays(1));

        Page<Movie> moviePage = new PageImpl<>(List.of(soon));

        // 'eq' fonksiyonu artık import edildiği için bu satır çalışacaktır
        when(movieRepository.findByStatus(eq(MovieStatus.COMING_SOON), any(Pageable.class)))
                .thenReturn(moviePage);

        List<Image> emptyImageList = Collections.emptyList();
        when(imageRepository.findByMovieId(soon.getId())).thenReturn(emptyImageList);

        when(movieMapper.mapMovieToMovieResponse(soon, emptyImageList))
                .thenReturn(MovieResponse.builder().id(1L).title("Soon").build());

        // when
        List<MovieResponse> out =
                movieService.getComingSoon(0, 10, "releaseDate", "asc");

        // then
        assertEquals(1, out.size());
        assertEquals(1L, out.get(0).getId());
    }
}