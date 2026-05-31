package com.aila.harvesttrack.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponseDto {

    private Integer totalCustomers;
    private Integer activeJobs;
    private Integer jobsCompletedToday;
    private Long todayEarnings;
}
