package com.cinemax.controller.user;

import com.cinemax.controller.businnes.TicketController;
import com.cinemax.payload.response.business.TicketResponse;
import com.cinemax.service.bussines.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TicketControllerTest {

    @Mock
    private TicketService ticketService;

    @Mock
    private Principal principal;

    @InjectMocks
    private  TicketController ticketController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(principal.getName()).thenReturn("member@example.com");
    }

    @Test
    void reserve_returnsOk() {
        Long showTimeId = 42L;
        String seatNo = "B12";

        TicketResponse resp = TicketResponse.builder()
                .id(1001L).movieTitle("Inception").hallName("Hall-1").seatNo(seatNo).build();

        when(ticketService.reserveByGet(showTimeId, seatNo, false, principal)).thenReturn(resp);

        ResponseEntity<TicketResponse> response =
                ticketController.reserve(showTimeId, seatNo, false, principal);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Inception", response.getBody().getMovieTitle());
        verify(ticketService).reserveByGet(showTimeId, seatNo, false, principal);
    }
}


