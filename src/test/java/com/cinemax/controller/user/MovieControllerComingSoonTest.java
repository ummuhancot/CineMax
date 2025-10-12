package com.cinemax.controller.user;

import com.cinemax.controller.businnes.MovieController;
import com.cinemax.payload.response.business.MovieResponse;
import com.cinemax.service.bussines.MovieService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MovieController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(MovieControllerComingSoonTest.TestCfg.class)
class MovieControllerComingSoonTest {

    @TestConfiguration
    static class TestCfg {
        @Bean MovieService movieService() { return Mockito.mock(MovieService.class); }
    }

    @Autowired MockMvc mockMvc;
    @Autowired MovieService movieService;

    @Test
    void ok_returnArray() throws Exception {
        when(movieService.getComingSoon(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(List.of(MovieResponse.builder().id(1L).title("Soon").build()));

        mockMvc.perform(get("/api/movies/coming-soon")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "releaseDate")   // ✅ 'release' doğru yazım
                        .param("type", "asc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}
