package com.cinemax.controller.businnes;

import com.cinemax.entity.concretes.business.Payment;
import com.cinemax.payload.request.business.PaymentRequest;
import com.cinemax.payload.request.business.TicketRequest;
import com.cinemax.payload.response.business.PaymentResponse;
import com.cinemax.service.bussines.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // Ödeme yapma işlemi rezervasyon sonrası
    @PostMapping("/pay")
    public ResponseEntity<Payment> makePayment(@RequestBody PaymentRequest request) {
        Payment payment = paymentService.makePayment(request);
        return ResponseEntity.ok(payment);
    }

}
