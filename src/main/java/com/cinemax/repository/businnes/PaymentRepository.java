package com.cinemax.repository.businnes;

import com.cinemax.entity.concretes.business.Payment;
import com.cinemax.entity.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByPaymentStatus(PaymentStatus status);

}
