package com.cinemax.service.bussines;

import com.cinemax.entity.concretes.business.Payment;
import com.cinemax.entity.concretes.business.Ticket;
import com.cinemax.entity.concretes.user.User;
import com.cinemax.entity.enums.PaymentStatus;
import com.cinemax.entity.enums.TicketStatus;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.mappers.PaymentMapper;
import com.cinemax.payload.request.business.PaymentRequest;
import com.cinemax.payload.response.business.PaymentResponse;
import com.cinemax.repository.businnes.PaymentRepository;
import com.cinemax.repository.businnes.TicketRepository;
import com.cinemax.service.helper.PaymentHelper;
import com.cinemax.service.helper.TicketHelper;
import com.cinemax.service.validator.PaymentValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentHelper paymentHelper;
    private final PaymentValidator paymentValidator;
    private final PaymentRepository paymentRepository;
    private final TicketRepository ticketRepository;
    private final TicketHelper ticketHelper;
    private final PaymentMapper paymentMapper;


    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        Ticket ticket = paymentHelper.getTicketOrThrow(request.getTicketId());

        Payment payment;

        // Eğer success alanı null ise -> sadece create (PENDING)
        if (request.getSuccess() == null) {
            payment = paymentMapper.toEntity(ticket);
            payment = paymentRepository.save(payment);
        } else {
            // Ödeme tamamlandıysa mevcut payment bulunur
            Optional<Payment> existing = paymentRepository.findByTicketId(ticket.getId());
            payment = existing.orElseGet(() -> paymentMapper.toEntity(ticket));
            payment = paymentHelper.completePaymentLogic(payment, request.getSuccess());
            payment = paymentRepository.save(payment);
        }

        return paymentMapper.toResponse(payment);
    }
}
