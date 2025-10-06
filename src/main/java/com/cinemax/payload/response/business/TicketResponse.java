package com.cinemax.payload.response.business;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TicketResponse {

    private Long id;
    private String username;
    private String movieTitle;
    private String hallName;
    private String seat;
    private Double price;
    private String status;       // PAID / RESERVED / CANCELLED
    private String date;
    private String showTime;
    private String createdAt;
    private Long paymentId;
    private LocalDateTime expiresAt;


}
