package com.cinemax.service.bussines;


import com.cinemax.entity.concretes.business.Payment;
import com.cinemax.entity.concretes.business.Ticket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    public void sendReservationConfirmation(Ticket ticket) {
        log.info("Reservation confirmed for ticket {} userId={}", ticket.getId(), ticket.getUser().getId());
    }

    public void sendReservationExpired(Ticket ticket) {
        log.info("Reservation expired for ticket {} userId={}", ticket.getId(), ticket.getUser().getId());
    }


}

