package com.cinemax.controller.businnes;

import com.cinemax.entity.concretes.business.Cinema;
import com.cinemax.payload.request.business.CinemaRequest;
import com.cinemax.payload.response.business.CinemaHallResponse;
import com.cinemax.payload.response.business.CinemaResponse;
import com.cinemax.payload.response.business.HallResponse;
import com.cinemax.service.bussines.CinemaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CinemaControllerTest {

    @Mock
    private CinemaService cinemaService;

    @InjectMocks
    private CinemaController cinemaController;

    private MockMvc mockMvc;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        mockMvc = MockMvcBuilders
                .standaloneSetup(cinemaController)
                .build();
    }

    // -------------------- GET /api/cinemas/city-hall --------------------
    @Test
    @DisplayName("GET /api/cinemas/city-hall -> list returns OK")
    void getCinemas_returnsList() throws Exception {
        String city = "İstanbul";
        String special = "IMAX";

        CinemaHallResponse r1 = CinemaHallResponse.builder()
                .id(1L)
                .name("Cinemax Şişli")
                .cityName("İstanbul")
                .specialHall("IMAX")
                .build();

        CinemaHallResponse r2 = CinemaHallResponse.builder()
                .id(2L)
                .name("Cinemax Kadıköy")
                .cityName("İstanbul")
                .specialHall("4DX")
                .build();

        when(cinemaService.getCinemas(city, special)).thenReturn(List.of(r1));

        mockMvc.perform(get("/api/cinemas/city-hall")
                        .param("city", city)
                        .param("specialHall", special))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Cinemax Şişli")))
                .andExpect(jsonPath("$[0].cityName", is("İstanbul")))
                .andExpect(jsonPath("$[0].specialHall", is("IMAX")));

        verify(cinemaService, times(1)).getCinemas(eq(city), eq(special));
    }

    // -------------------- POST /api/cinemas/save --------------------
    @Test
    @DisplayName("POST /api/cinemas/save -> created")
    void createCinema_created() throws Exception {
        CinemaRequest req = new CinemaRequest();
        req.setName("Şişli Cinemax");
        req.setCityName("İstanbul");
        req.setAddress("Büyükdere Cd. No:1");
        req.setPhoneNumber("0212 000 00 00");
        req.setEmail("sisli@cinemax.com");

        CinemaResponse resp = CinemaResponse.builder()
                .id(10L)
                .name("Şişli Cinemax")
                .cityName("İstanbul")
                .address("Büyükdere Cd. No:1")
                .phoneNumber("0212 000 00 00")
                .email("sisli@cinemax.com")
                .build();

        when(cinemaService.createCinema(Mockito.<CinemaRequest>any())).thenReturn(resp);

        mockMvc.perform(post("/api/cinemas/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.name", is("Şişli Cinemax")))
                .andExpect(jsonPath("$.cityName", is("İstanbul")));

        verify(cinemaService, times(1)).createCinema(Mockito.<CinemaRequest>any());
    }

    // -------------------- DELETE /api/cinemas/{cityId}/auth/{cinemaId} --------------------
    @Test
    @DisplayName("DELETE /api/cinemas/{cityId}/auth/{cinemaId} -> ok")
    void deleteCinema_ok() throws Exception {
        Long cityId = 34L;
        Long cinemaId = 7L;

        when(cinemaService.deleteCinema(cityId, cinemaId)).thenReturn(new Cinema());

        mockMvc.perform(delete("/api/cinemas/{cityId}/auth/{cinemaId}", cityId, cinemaId))
                .andExpect(status().isOk());

        verify(cinemaService, times(1)).deleteCinema(eq(cityId), eq(cinemaId));
    }

    // -------------------- PUT /api/cinemas/update/{id} --------------------
    @Test
    @DisplayName("PUT /api/cinemas/update/{id} -> ok")
    void updateCinema_ok() throws Exception {
        Long id = 5L;
        CinemaRequest req = new CinemaRequest();
        req.setName("Güncel Cinemax");
        req.setCityName("İstanbul");
        req.setAddress("Yeni Adres 123");
        req.setPhoneNumber("0212 111 11 11");
        req.setEmail("guncel@cinemax.com");

        CinemaResponse resp = CinemaResponse.builder()
                .id(id)
                .name("Güncel Cinemax")
                .cityName("İstanbul")
                .address("Yeni Adres 123")
                .phoneNumber("0212 111 11 11")
                .email("guncel@cinemax.com")
                .build();

        when(cinemaService.updateCinema(eq(id), Mockito.<CinemaRequest>any())).thenReturn(resp);

        mockMvc.perform(put("/api/cinemas/update/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.name", is("Güncel Cinemax")));

        verify(cinemaService, times(1)).updateCinema(eq(id), Mockito.<CinemaRequest>any());
    }

    // -------------------- GET /api/cinemas/{id} --------------------
    @Test
    @DisplayName("GET /api/cinemas/{id} -> ok")
    void getCinemaById_ok() throws Exception {
        Long id = 9L;
        CinemaResponse resp = CinemaResponse.builder()
                .id(id)
                .name("Kadıköy Cinemax")
                .cityName("İstanbul")
                .build();

        when(cinemaService.getCinemaById(id)).thenReturn(resp);

        mockMvc.perform(get("/api/cinemas/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(9)))
                .andExpect(jsonPath("$.name", is("Kadıköy Cinemax")));

        verify(cinemaService, times(1)).getCinemaById(eq(id));
    }

    // -------------------- GET /api/cinemas/{id}/halls --------------------
    @Test
    @DisplayName("GET /api/cinemas/{id}/halls -> list ok")
    void getHallsByCinema_ok() throws Exception {
        Long cinemaId = 3L;

        HallResponse h1 = HallResponse.builder().id(100L).name("Salon 1").build();
        HallResponse h2 = HallResponse.builder().id(101L).name("Salon 2").build();

        when(cinemaService.getHallsByCinemaId(cinemaId)).thenReturn(List.of(h1, h2));

        mockMvc.perform(get("/api/cinemas/{id}/halls", cinemaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(100)))
                .andExpect(jsonPath("$[0].name", is("Salon 1")));

        verify(cinemaService, times(1)).getHallsByCinemaId(eq(cinemaId));
    }

    // -------------------- GET /api/cinemas/specialhalls/{cinemaId} --------------------
    @Test
    @DisplayName("GET /api/cinemas/specialhalls/{cinemaId} -> list ok")
    void getSpecialHallsByCinema_ok() throws Exception {
        Long cinemaId = 11L;

        HallResponse special = HallResponse.builder().id(200L).name("IMAX Salon").build();

        when(cinemaService.getSpecialHallsByCinemaId(cinemaId)).thenReturn(List.of(special));

        mockMvc.perform(get("/api/cinemas/specialhalls/{cinemaId}", cinemaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("IMAX Salon")));

        verify(cinemaService, times(1)).getSpecialHallsByCinemaId(eq(cinemaId));
    }
}
