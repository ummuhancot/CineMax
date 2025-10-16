package com.cinemax.payload.request.business;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
public class HallRequest {
    @NotBlank(message = "Hall name cannot be blank")
    private String name;

    @NotNull(message = "Seat capacity cannot be null")
    private Integer seatCapacity;

    private Boolean isSpecial = false;

    @NotBlank(message = "Type cannot be blank")
    private String type;

    @NotNull(message = "Cinema ID cannot be null")
    private Long cinemaId;

}
