package com.cinemax.payload.mappers;

import com.cinemax.entity.concretes.business.Ticket;
import com.cinemax.payload.response.business.TicketResponse;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    public TicketResponse toResponse(Ticket t) {
        return TicketResponse.builder()
                .id(t.getId())
                .movieTitle(t.getShowTime()!=null && t.getShowTime().getMovie()!=null
                        ? t.getShowTime().getMovie().getTitle() : null)
                .hallName(t.getShowTime()!=null && t.getShowTime().getHall()!=null
                        ? t.getShowTime().getHall().getName() : null)
                .seatNo(t.getSeatNo())
                .status(t.getStatus())
                .reservedAt(t.getReservedAt())
                .purchasedAt(t.getPurchasedAt())
                .showStart(t.getShowTime()!=null ? t.getShowTime().getStartTime() : null) // alan adın farklıysa uyarlayın
                .build();
    }
}

