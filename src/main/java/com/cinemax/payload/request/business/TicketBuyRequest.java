package com.cinemax.payload.request.business;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/** T-4 POST /api/tickets/buy-ticket body */
@Data
public class TicketBuyRequest {

    @NotNull
    private Long showTimeId;

    @NotBlank
    private  String seatNo;


}
