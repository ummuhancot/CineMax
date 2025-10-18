package com.cinemax.scheduler;


import com.cinemax.entity.concretes.business.Ticket;
import com.cinemax.entity.enums.TicketStatus;
import com.cinemax.repository.businnes.TicketRepository;
import com.cinemax.service.bussines.NotificationService;
import com.cinemax.service.statusmanager.TicketStatusManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationExpirationScheduler {

    private final TicketRepository ticketRepository;
    private final TicketStatusManager statusManager;
    private final NotificationService notificationService;

    // Her dakika kontrol et (projede uygun olarak değiştir)
    @Scheduled(fixedRateString = "${cinemax.reservation.expire.check-ms:600000}") // 10 dakika
    public void checkAndExpire() {
        List<Ticket> expired = ticketRepository.findAllByTicketStatusAndExpiresAtBefore(TicketStatus.RESERVED, LocalDateTime.now());
        for (Ticket t : expired) {
            statusManager.setExpired(t);
            ticketRepository.save(t);
            notificationService.sendReservationExpired(t);
            log.info("Expired reservation: ticketId={}", t.getId());
        }
    }
}
