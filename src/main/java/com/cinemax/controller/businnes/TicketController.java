package com.cinemax.controller.businnes;


import com.cinemax.payload.request.business.TicketBuyRequest;
import com.cinemax.payload.response.business.TicketResponse;
import com.cinemax.service.bussines.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    // T-3: GET /api/tickets/reserve/{id}?seatNo=B12[&useMovieId=true]
    @GetMapping("/reserve/{id}")
    public ResponseEntity<TicketResponse> reserve(
            @PathVariable("id") Long id,
            @RequestParam("seatNo") String seatNo,
            @RequestParam(value = "useMovieId", defaultValue = "false") boolean useMovieId,
            Principal principal
    ) {
        return ResponseEntity.ok(ticketService.reserveByGet(id, seatNo, useMovieId, principal));
    }

    /**
     * T-4: POST /api/tickets/buy-ticket
     */
    @PostMapping("/buy-ticket")
    public ResponseEntity<TicketResponse> buyTicket(@Validated @RequestBody TicketBuyRequest request,
                                                    Principal principal) {
        TicketResponse resp = ticketService.buy(request, principal);
        return new ResponseEntity<>(resp, HttpStatus.CREATED);
    }
}
