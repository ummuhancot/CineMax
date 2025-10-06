package com.cinemax.payload.request.business;

import com.cinemax.entity.enums.TicketStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TicketRequest {

    @NotNull(message = "Movie ID cannot be null")
    private Long movieId;

    @NotNull(message = "Hall ID cannot be null")
    private Long hallId;

    @NotNull(message = "ShowTime ID cannot be null")
    private Long showTimeId;

    @NotBlank(message = "Seat letter is required")
    private String seatLetter;

    @Min(value = 1, message = "Seat number must be at least 1")
    private int seatNumber;

    @NotNull(message = "Price cannot be null")
    private Double price;

    private Long paymentId;
}
