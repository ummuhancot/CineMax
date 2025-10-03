package com.cinemax.payload.response.business;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CinemaHallResponse {

    private Long id;
    private String name;
    private String slug;
    private String address;
    private String phoneNumber;
    private String email;
    private String cityName;
    private String specialHall;

}
