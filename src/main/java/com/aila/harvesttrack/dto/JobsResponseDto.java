package com.aila.harvesttrack.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobsResponseDto {
    private Integer id;
    private String customerName;
    private String village;
    private String crop;
    private String acres;
    private String rate;
    private String amount;
    private String duration;
    private String startTime;
    private String endTime;
    private String date;
    private String status;
    private String notes;
}
