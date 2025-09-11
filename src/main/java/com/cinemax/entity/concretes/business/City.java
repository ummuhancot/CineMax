package com.cinemax.entity.concretes.business;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cities")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull
    @Size(max = 30, message = "City name cannot be longer than 30 characters")
    private String name;

    //@JoinColumn(name = "country_id")
    //private Country country;


    /**
     CITY
     Field	Type	Properties
     userId	Long(PK)	Auto increment
     name	String	Not null, max: 30
     countryId	Long(FK)

     */
}
