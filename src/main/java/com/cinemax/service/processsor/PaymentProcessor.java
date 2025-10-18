package com.cinemax.service.processsor;


import com.cinemax.entity.concretes.business.Payment;
import com.cinemax.entity.enums.PaymentStatus;
import com.cinemax.exception.InvalidPaymentException;
import com.cinemax.payload.request.business.PaymentRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

@Component
@Slf4j
public class PaymentProcessor {

    private final Random rnd = new Random();

    /**
     * Simüle edilmiş payment işlemi.
     * Gerçekte ödeme gateway çağrılacak.
     */
    public Payment process(PaymentRequest req) {
        boolean success = simulateGateway(req);
        Payment p = Payment.builder()
                .amount(req.getAmount())
                .paymentDate(LocalDateTime.now())
                .paymentStatus(success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED)
                .build();

        if (!success) {
            throw new InvalidPaymentException("Ödeme işlemi başarısız oldu (simülasyon).");
        }
        return p;
    }

    private boolean simulateGateway(PaymentRequest req) {
        // %95 başarı demo faktörü
        return rnd.nextInt(100) < 95;
    }
}
