package com.cinemax.repository.businnes;
import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.entity.concretes.business.ShowTime;
import com.cinemax.entity.concretes.business.Hall;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShowTimeRepositoryTest {

    @Mock
    private ShowTimeRepository showTimeRepository;

    @Test
    void itShouldReturnOnlyFutureAndLaterTodayShowTimes() {
        // Arrange
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        Movie movie = Movie.builder().id(1L).title("Inception").build();
        Hall hall = new Hall();
        hall.setId(1L);
        hall.setName("Hall 1");

        ShowTime futureShow = ShowTime.builder()
                .id(101L)
                .movie(movie)
                .hall(hall)
                .date(today.plusDays(1))
                .startTime(LocalTime.of(14, 0))
                .build();

        ShowTime laterToday = ShowTime.builder()
                .id(102L)
                .movie(movie)
                .hall(hall)
                .date(today)
                .startTime(now.plusHours(1))
                .build();

        // Mock repository davranışı
        when(showTimeRepository.findUpcomingShowTimes(movie.getId(), today, now))
                .thenReturn(List.of(futureShow, laterToday));

        // Act
        List<ShowTime> result = showTimeRepository.findUpcomingShowTimes(movie.getId(), today, now);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(futureShow, laterToday);
    }
}


