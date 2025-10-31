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
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieServiceComingSoonTest {

    @Mock
    private MovieRepository movieRepository;
    @Mock
    private HallRepository hallRepository;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private ShowTimeRepository showTimeRepository;
    @Mock
    private MovieMapper movieMapper;
    @Mock
    private ShowTimeMapper showTimeMapper;
    @Mock
    private MovieShowTimesMapper movieShowTimesMapper;

    @InjectMocks
    private MovieService movieService;

    @Test
    void returnsOnlyComingSoon() {
        // given
        Movie soon = new Movie();
        soon.setId(1L);
        soon.setTitle("Soon");
        soon.setStatus(MovieStatus.COMING_SOON);
        soon.setReleaseDate(LocalDate.now().plusDays(7));

        Movie now = new Movie();
        now.setId(2L);
        now.setTitle("Now");
        now.setStatus(MovieStatus.IN_THEATERS);
        now.setReleaseDate(LocalDate.now().minusDays(1));

        // Repository sadece iki filmi döndürüyor
        when(movieRepository.findAll(any(Sort.class)))
                .thenReturn(List.of(soon, now));

        // Image repository her film için boş liste döndürsün
        when(imageRepository.findByMovieId(any(Long.class)))
                .thenReturn(List.of());

        // Mapper çağrısı artık iki parametreli: Movie + List<Image>
        when(movieMapper.mapMovieToMovieResponse(eq(soon), anyList()))
                .thenReturn(MovieResponse.builder().id(1L).title("Soon").build());

        // when
        List<MovieResponse> out = movieService.getComingSoon(0, 10, "releaseDate", "asc");

        // then
        assertEquals(1, out.size());
        assertEquals(1L, out.get(0).getId());
        assertEquals("Soon", out.get(0).getTitle());
    }
}
