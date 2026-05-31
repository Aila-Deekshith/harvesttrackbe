package com.aila.harvesttrack.service;

import com.aila.harvesttrack.dto.DashboardResponseDto;
import com.aila.harvesttrack.dto.ReportDashboardResponseDto;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface DashboardService {

    public DashboardResponseDto getDashboardMetrics(Integer ownerId);

    public ReportDashboardResponseDto generateDashboardReport(Integer ownerId, Instant from, Instant to);

    List<Map<Instant, Long>> dailyEarnings(Integer ownerId, Instant from, Instant to);
}
