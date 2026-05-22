package com.aila.harvesttrack.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleRequestDto {

    private String    name;
    private String    type;
    private String    registrationNumber;
    private String    fuelType;
    private LocalDate purchaseDate;
    private Integer   ownerId;
}