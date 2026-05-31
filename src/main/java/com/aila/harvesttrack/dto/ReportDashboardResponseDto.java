package com.aila.harvesttrack.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportDashboardResponseDto {

    private Long totalEarnings;
    private Integer totalJobs;
    private Integer totalAcres;
    private Integer totalHours;
    private Long avgAmountPerJob;
    private Long avgAmountPerAcre;
}
