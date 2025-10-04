package com.cinemax.payload.request.business;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class TicketRequest {
    @NotNull(message = "ShowTime ID cannot be null")
    private  Long showTimeId;

    @NotNull(message = "Count cannot be null")
    @Min(value = 1,message = "At least one ticket must be purchased")
    private  Integer count;

    private List<String> seatInformation;
}
