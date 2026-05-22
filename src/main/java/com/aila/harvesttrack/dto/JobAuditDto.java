package com.aila.harvesttrack.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobAuditDto {
    private Long id;
    private Integer jobId;
    private String activityType;
    private Instant eventTimestamp;
    private String note;
}