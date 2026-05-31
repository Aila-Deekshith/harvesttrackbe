package com.aila.harvesttrack.controller;

import com.aila.harvesttrack.dto.ApiResponse;
import com.aila.harvesttrack.dto.DashboardResponseDto;
import com.aila.harvesttrack.dto.ReportDashboardResponseDto;
import com.aila.harvesttrack.service.impl.DashboardServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardServiceImpl dashboardService;

    @GetMapping
    public DashboardResponseDto getDashboardData(@RequestParam Integer ownerId) {
        return dashboardService.getDashboardMetrics(ownerId);
    }

    @GetMapping("/report")
    public ResponseEntity<ApiResponse<ReportDashboardResponseDto>> getDashboardReport(@RequestParam Integer ownerId, @RequestParam(required = false) Instant from, @RequestParam(required = false) Instant to) {
        try {
            ReportDashboardResponseDto reportDashboardResponseDto = dashboardService.generateDashboardReport(ownerId, from, to);
            return ResponseEntity.ok(ApiResponse.success("Dashboard report generated successfully", reportDashboardResponseDto));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to generate dashboard report: " + e.getMessage()));
        }
    }

    @GetMapping("/dailyEarnings")
    public ResponseEntity<ApiResponse<List<Map<Instant, Long>>>> getDailyEarnings(@RequestParam Integer ownerId, Instant from, Instant to) {
        try {
            List<Map<Instant, Long>> dailyEarnings = dashboardService.dailyEarnings(ownerId, from, to);
            return ResponseEntity.ok(ApiResponse.success("Daily earnings fetched successfully", dailyEarnings));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to fetch daily earnings: " + e.getMessage()));
        }
    }


}
