package com.cinemax.controller.businnes;

import com.cinemax.payload.request.business.PaymentRequest;
import com.cinemax.payload.response.business.PaymentResponse;
import com.cinemax.service.bussines.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Rezervasyonu öder — ticket RESERVED durumundaysa PAID olur.
     */
    @PostMapping("/pay")
    public ResponseEntity<PaymentResponse> makePayment(@RequestBody PaymentRequest request) {
        PaymentResponse paymentResponse = paymentService.makePayment(request);
        return ResponseEntity.ok(paymentResponse);
    }

    /**
     * Çoklu bilet için ödeme yap
     * Tek bir request içinde birden fazla TicketRequest gönderilir
     */
    @PostMapping("/pay-multiple")
    public ResponseEntity<List<PaymentResponse>> makeMultiplePayments(
            @RequestBody List<PaymentRequest> requests) {
        List<PaymentResponse> responses = paymentService.makeMultiplePayments(requests);
        return ResponseEntity.ok(responses);
    }

    /**
        * Ödeme başarısız olur — ticket RESERVED durumundaysa CANCELLED olur.
     *  */
    @PostMapping("/fail/{id}")
    public ResponseEntity<PaymentResponse> failPayment(@PathVariable Long id) {
        PaymentResponse paymentResponse = paymentService.failPayment(id);
        return ResponseEntity.ok(paymentResponse);
    }


    @GetMapping("/successful")
    public ResponseEntity<List<PaymentResponse>> getSuccessfulPayments() {
        return ResponseEntity.ok(paymentService.getSuccessfulPayments());
    }

    @GetMapping("/successful/{id}")
    public ResponseEntity<PaymentResponse> getSuccessfulPaymentById(@PathVariable Long id) {
        PaymentResponse payment = paymentService.getSuccessfulPaymentById(id);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/failed")
    public ResponseEntity<List<PaymentResponse>> getFailedPayments() {
        return ResponseEntity.ok(paymentService.getFailedPayments());
    }

    @GetMapping("/failed/{id}")
    public ResponseEntity<PaymentResponse> getFailedPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getFailedPaymentById(id));
    }
}
