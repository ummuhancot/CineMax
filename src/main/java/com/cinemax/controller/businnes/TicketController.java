package com.cinemax.controller.businnes;

import com.cinemax.entity.concretes.business.Ticket;
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

    // 🔹 Bilet rezerve etme
    //
    @PostMapping("/reserve")
    public ResponseEntity<Ticket> reserveTicket(@RequestBody TicketRequest request) {
        Ticket ticket = ticketService.reserveTicket(request);
        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/all/reserved")
    public ResponseEntity<List<TicketResponse>> getAllReservedTickets() {
        return ResponseEntity.ok(ticketService.getAllReservedTickets());
    }

    @GetMapping("/all/cancelled")
    public ResponseEntity<List<TicketResponse>> getAllCancelledTickets() {
        return ResponseEntity.ok(ticketService.getAllCancelledTickets());
    }

    //kodda bilet alma yazılmadı
    //bu mantık için önce boş biletler oluşturulup paymend de sadece satın alınacak mantığı daha mantıklı
    @GetMapping("/all/paid")
    public ResponseEntity<List<TicketResponse>> getAllPaidTickets() {
        return ResponseEntity.ok(ticketService.getAllPaidTickets());
    }


    // 🔹 Tüm biletleri getir
    @GetMapping("/user/{userId}/all")
    public ResponseEntity<List<TicketResponse>> getUserAllTickets(@PathVariable Long userId) {
        return ResponseEntity.ok(ticketService.getAllTickets(userId));
    }

    // 🔹 Reserved biletler
    @GetMapping("/user/{userId}/reserved")
    public ResponseEntity<List<TicketResponse>> getUserReservedTickets(@PathVariable Long userId) {
        return ResponseEntity.ok(ticketService.getReservedTickets(userId));
    }

    //bilet alma yazılmadı
    // 🔹 Paid biletler
    @GetMapping("/user/{userId}/paid")
    public ResponseEntity<List<TicketResponse>> getUserPaidTickets(@PathVariable Long userId) {
        return ResponseEntity.ok(ticketService.getPaidTickets(userId));
    }

    // 🔹 Cancelled biletler
    @GetMapping("/user/{userId}/cancelled")
    public ResponseEntity<List<TicketResponse>> getUserCancelledTickets(@PathVariable Long userId) {
        return ResponseEntity.ok(ticketService.getCancelledTickets(userId));
    }





}
