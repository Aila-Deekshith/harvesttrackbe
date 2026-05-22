package com.aila.harvesttrack.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobsRequestDto {

    private Integer customerId;
    private Integer vehicleId;
    private String  description;
    private String  status;
    private Instant startDate;
    private Instant endDate;
    private Integer activityId;
    private Float acres;
    private Float cost;
}