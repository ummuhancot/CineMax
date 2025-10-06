package com.cinemax.controller.businnes;

import com.cinemax.payload.request.business.TicketRequest;
import com.cinemax.payload.response.business.TicketResponse;
import com.cinemax.service.bussines.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    //“Kullanıcı giriş yaptıktan sonra bir koltuğu başarıyla rezerve edebilmeli; rezervasyon süresi boyunca koltuk güvenle tutulmalı ve süresi dolduğunda koltuk otomatik olarak tekrar alınabilir hâle gelmeli.”
    @PostMapping("/reserve")
    public ResponseEntity<TicketResponse> saveRezerve(
            @RequestBody @Valid TicketRequest request,
            Principal principal
    ) {
        // principal.getName() login olan kullanıcının username'ini verir
        String username = principal.getName();
        TicketResponse response = ticketService.reserveTicket(request, username);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


}
