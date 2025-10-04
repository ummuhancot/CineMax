package com.cinemax.payload.response.business;

import com.cinemax.entity.enums.TicketStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TicketResponse {
    private Long id;
    private String movieTitle;
    private String hallName;
    private String seatNo;
    private TicketStatus status;
    private LocalDateTime reservedAt;
    private LocalDateTime purchasedAt;
    private LocalDateTime showStart;
}
