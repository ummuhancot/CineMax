package com.cinemax.payload.request.business;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@NoArgsConstructor
@SuperBuilder
public class BuyTicketRequest {

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotNull(message = "Showtime ID cannot be null")
    private Long showTimeId;

    @NotBlank(message = "Seat letter cannot be blank")
    private String seatLetter;

    @NotNull(message = "Seat number cannot be null")
    @Positive(message = "Seat number must be positive")
    private Integer seatNumber;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotNull(message = "Ticket ID cannot be null")
    private Long ticketId;

    // ---- Fake Credit Card Info (for simulation) ----

    @NotBlank(message = "Card number cannot be blank")
    @Size(min = 12, max = 19, message = "Card number must be between 12 and 19 digits")
    private String cardNumber;

    @NotBlank(message = "Card expiry cannot be blank")
    @Pattern(regexp = "\\d{2}/\\d{2}", message = "Card expiry must be in MM/YY format")
    private String cardExpiry;

    @NotBlank(message = "CVC cannot be blank")
    @Size(min = 3, max = 4, message = "CVC must be 3 or 4 digits")
    private String cardCvc;
}
