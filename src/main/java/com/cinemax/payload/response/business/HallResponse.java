package com.cinemax.payload.response.business;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HallResponse {

    private Long id;
    private String name;
    private Integer seatCapacity;
    private Boolean isSpecial;
    private String type;
    private String cinemaName;
}
