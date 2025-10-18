package com.cinemax.controller.businnes;

import com.cinemax.payload.request.business.TicketRequest;
import com.cinemax.payload.response.business.TicketResponse;
import com.cinemax.service.bussines.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    /**
     * Yeni bir bilet rezervasyonu oluşturur ve TicketResponse döndürür.
     */
    @PostMapping("/reserve")
    public ResponseEntity<TicketResponse> reserveTicket(@RequestBody TicketRequest request) {
        TicketResponse ticketResponse = ticketService.reserveTicket(request);
        return ResponseEntity.ok(ticketResponse);
    }

    /**
     * Mevcut bir rezervasyonu iptal eder ve TicketResponse döndürür.
     */
    @PostMapping("/cancel/{id}")
    public ResponseEntity<TicketResponse> cancelReservation(@PathVariable Long id) {
        TicketResponse ticketResponse = ticketService.cancelReservation(id);
        return ResponseEntity.ok(ticketResponse);
    }

}
