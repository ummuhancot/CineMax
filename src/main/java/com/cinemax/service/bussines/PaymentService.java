package com.cinemax.service.bussines;

import com.cinemax.entity.concretes.business.Payment;
import com.cinemax.entity.concretes.business.Ticket;
import com.cinemax.entity.concretes.user.User;
import com.cinemax.entity.enums.PaymentStatus;
import com.cinemax.entity.enums.TicketStatus;
import com.cinemax.payload.mappers.PaymentMapper;
import com.cinemax.payload.mappers.TicketMapper;
import com.cinemax.payload.messages.ErrorMessages;
import com.cinemax.payload.request.business.PaymentRequest;
import com.cinemax.payload.response.business.PaymentResponse;
import com.cinemax.repository.businnes.PaymentRepository;
import com.cinemax.repository.businnes.TicketRepository;
import com.cinemax.service.helper.PaymentHelper;
import com.cinemax.service.processsor.PaymentProcessor;
import com.cinemax.service.statusmanager.TicketStatusManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Payment ile ilgili iş kurallarını tanımlar.
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TicketRepository ticketRepository;
    private final PaymentProcessor paymentProcessor;
    private final PaymentMapper paymentMapper;
    private final TicketMapper ticketMapper;
    private final TicketStatusManager ticketStatusManager;
    private final PaymentHelper paymentHelper;

    /**
     * Bir rezervasyon için ödeme yapar.
     * Ticket status kontrol edilir, ödeme onaylanır, bilet PAID yapılır.
     * @param request PaymentRequest DTO
     * @return Ödeme sonucu Payment entity
     */
    @Transactional
    public PaymentResponse makePayment(PaymentRequest request) {

        Ticket ticket = paymentHelper.getTicketOrThrow(request.getTicketId());
        if (ticket.getTicketStatus() != TicketStatus.RESERVED) {
            throw new IllegalStateException(ErrorMessages.TICKET_CANNOT_BE_PAID);
        }
        User user = paymentHelper.getUserOrThrow(request.getUserId());

        Double ticketPrice = ticket.getPrice();
        Double receivedAmount = request.getAmount();
        Double change = receivedAmount - ticketPrice;

        if (change < 0) {
            throw new IllegalArgumentException(ErrorMessages.INSUFFICIENT_PAYMENT);
        }

        Payment payment = paymentProcessor.process(request);
        payment.setTicket(ticket);
        payment.setUser(user);
        payment.setPaymentStatus(PaymentStatus.SUCCESS);

        // Ticket status update
        // Ticket status update
        ticket = ticketStatusManager.setPaid(ticket);  // setPaid hem status'u PAID yapar hem expiresAt'ı null eder
        ticket.setPayment(payment);


        paymentRepository.save(payment);
        ticketRepository.save(ticket);

        // ✅ Mapper ile PaymentResponse üret, change’i de gönder
        return paymentMapper.toResponse(payment, ticketMapper, change);
    }

    @Transactional
    public List<PaymentResponse> makeMultiplePayments(List<PaymentRequest> requests) {
        List<PaymentResponse> responses = new ArrayList<>();

        for (PaymentRequest request : requests) {
            Ticket ticket = paymentHelper.getTicketOrThrow(request.getTicketId());
            if (ticket.getTicketStatus() != TicketStatus.RESERVED) {
                throw new IllegalStateException("Bu bilet ödenemez: Ticket durumu uygun değil.");
            }

            User user = paymentHelper.getUserOrThrow(request.getUserId());

            Double ticketPrice = ticket.getPrice();
            Double receivedAmount = request.getAmount();
            Double change = receivedAmount - ticketPrice;

            if (change < 0) {
                throw new IllegalArgumentException("Yetersiz ödeme.");
            }

            // Ödeme işlemi
            Payment payment = paymentProcessor.process(request);
            payment.setTicket(ticket);
            payment.setUser(user);
            payment.setPaymentStatus(PaymentStatus.SUCCESS);

            // Ticket status update
            ticket = ticketStatusManager.setPaid(ticket);  // PAID + expiresAt null
            ticket.setPayment(payment);

            paymentRepository.save(payment);
            ticketRepository.save(ticket);

            // Response ekle
            responses.add(paymentMapper.toResponse(payment, ticketMapper, change));
        }

        return responses;
    }


    /**
     * Ödemeyi FAILED durumuna geçirir (simülasyon veya test amaçlı).
     * @param paymentId Payment ID
     * @return Güncellenmiş Payment entity
     */

    @Transactional
    public PaymentResponse failPayment(Long paymentId) {
        Payment payment = paymentHelper.getPaymentOrThrow(paymentId);
        Ticket ticket = payment.getTicket();
        if (ticket != null) {
            ticket.setTicketStatus(TicketStatus.CANCELLED);
        }
        payment.setPaymentStatus(PaymentStatus.FAILED);
        // DB kaydı
        paymentRepository.save(payment);
        if (ticket != null) {
            ticketRepository.save(ticket);
        }
        // Mapper ile PaymentResponse döndür
        return paymentMapper.toResponse(payment, ticketMapper,null);
    }

    public List<PaymentResponse> getSuccessfulPayments() {
        return paymentRepository.findByPaymentStatus(PaymentStatus.SUCCESS).stream()
                .map(paymentMapper::toPaymentResponse)
                .collect(Collectors.toList());
    }

    public PaymentResponse getSuccessfulPaymentById(Long id) {
        Payment payment = paymentHelper.getPaymentOrThrow(id);
        if (payment.getPaymentStatus() != PaymentStatus.SUCCESS) {
            throw new IllegalStateException("Payment is not successful");
        }

        return paymentMapper.toPaymentResponse(payment);
    }

    // Başarısız ödemeleri getir
    public List<PaymentResponse> getFailedPayments() {
        return paymentRepository.findByPaymentStatus(PaymentStatus.FAILED).stream()
                .map(paymentMapper::toPaymentResponse)
                .collect(Collectors.toList());
    }

    // ID’ye göre FAILED kontrolü
    public PaymentResponse getFailedPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));

        if (payment.getPaymentStatus() != PaymentStatus.FAILED) {
            throw new IllegalStateException("Payment is not failed");
        }

        return paymentMapper.toPaymentResponse(payment);
    }
}