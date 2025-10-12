package com.cinemax.service.businnes;

import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.payload.mappers.MovieMapper;
import com.cinemax.payload.response.business.MovieResponse;
import com.cinemax.repository.businnes.HallRepository;
import com.cinemax.repository.businnes.ImageRepository;
import com.cinemax.repository.businnes.MovieRepository;
import com.cinemax.repository.businnes.ShowTimeRepository;
import com.cinemax.payload.mappers.MovieShowTimesMapper;
import com.cinemax.payload.mappers.ShowTimeMapper;
import com.cinemax.service.bussines.MovieService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieServiceSearchTest {

    @Mock private MovieRepository movieRepository;
    @Mock private HallRepository hallRepository;
    @Mock private ImageRepository imageRepository;
    @Mock private ShowTimeRepository showTimeRepository;
    @Mock private MovieMapper movieMapper;
    @Mock private ShowTimeMapper showTimeMapper;
    @Mock private MovieShowTimesMapper movieShowTimesMapper;

    @InjectMocks
    private MovieService movieService;


    @Test
    void search_returnsMoviesWithPagingAndMapping() {
        // arrange
        String q = "maria";
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "title"));

        Movie m1 = new Movie();
        m1.setId(1L);
        m1.setTitle("Maria");

        Movie m2 = new Movie();
        m2.setId(2L);
        m2.setTitle("Another Maria");

        Page<Movie> page = new PageImpl<>(List.of(m1, m2), pageable, 2);

        when(movieRepository.search(anyString(), any(Pageable.class))).thenReturn(page);

        when(movieMapper.mapMovieToMovieResponse(m1))
                .thenReturn(MovieResponse.builder().id(1L).title("Maria").build());
        when(movieMapper.mapMovieToMovieResponse(m2))
                .thenReturn(MovieResponse.builder().id(2L).title("Another Maria").build());

        // act
        List<MovieResponse> result =
                movieService.searchMovies(q, 0, 10, "title", "desc");

        // assert
        assertEquals(2, result.size());
        assertEquals("Maria", result.get(0).getTitle());
        assertEquals("Another Maria", result.get(1).getTitle());
    }
}
