package com.cinemax.controller.businnes;

import com.cinemax.payload.request.business.TicketRequest;
import com.cinemax.payload.response.business.TicketResponse;
import com.cinemax.service.bussines.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
     * Çoklu rezervasyon oluşturur.
     * @param request TicketRequest DTO, seats listesi dolu olmalı
     * @return Rezervasyon yapılan biletlerin listesi
     * Birkişinin birden fazla bilet alması methodu
     */
    @PostMapping("/multiple")
    @PreAuthorize("hasAnyAuthority('Admin','Manager','User')")
    public ResponseEntity<List<TicketResponse>> reserveMultipleTickets(
            @Valid @RequestBody TicketRequest request) {

        List<TicketResponse> tickets = ticketService.reserveMultipleTickets(request);
        return ResponseEntity.ok(tickets);
    }

    @PostMapping("/reserve-multiple")
    @PreAuthorize("hasAnyAuthority('Admin','Manager','User')")
    public ResponseEntity<List<TicketResponse>> reserveMultipleTickets(
            @RequestBody List<TicketRequest> requests) {
        List<TicketResponse> responses = ticketService.reserveUserMultipleTickets(requests);
        return ResponseEntity.ok(responses);
    }



    /**
     * Mevcut bir rezervasyonu iptal eder ve TicketResponse döndürür.
     */
    @PostMapping("/cancel/{id}")
    public ResponseEntity<TicketResponse> cancelReservation(@PathVariable Long id) {
        TicketResponse ticketResponse = ticketService.cancelReservation(id);
        return ResponseEntity.ok(ticketResponse);
    }

    /**
     * Tüm biletleri getirir.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<TicketResponse>> getAllTickets() {
        List<TicketResponse> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }

    /**
     * ID'ye göre tek bir bileti getirir.
     */
    @GetMapping("/getById/{id}")
    public ResponseEntity<TicketResponse> getTicketById(@PathVariable Long id) {
        TicketResponse ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(ticket);
    }

    /**
     * Belirli statüdeki biletleri getirir (ör: RESERVED, PAID, CANCELLED, EXPIRED).
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TicketResponse>> getTicketsByStatus(@PathVariable String status) {
        List<TicketResponse> tickets = ticketService.getTicketsByStatus(status);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<List<TicketResponse>> getTicketsByUserAndStatus(
            @PathVariable Long userId,
            @PathVariable String status) {
        List<TicketResponse> tickets = ticketService.getTicketsByUserAndStatus(userId, status);
        return ResponseEntity.ok(tickets);
    }


    /**
     * Mevcut bir bileti günceller.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<TicketResponse> updateTicket(
            @PathVariable Long id,
            @RequestBody TicketRequest request) {
        TicketResponse updatedTicket = ticketService.updateTicket(id, request);
        return ResponseEntity.ok(updatedTicket);
    }
}
