package com.aila.harvesttrack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponseDTO {

    private Integer id;
    private String name;
    private String phone;
    private String address;
    private Instant createdAt;
    private Instant updatedAt;
    private String deletedAt;
    private Integer jobsCount;
    private Float acres;
    private Float amount;
}
