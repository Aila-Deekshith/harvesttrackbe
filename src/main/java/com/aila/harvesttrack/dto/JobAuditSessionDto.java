package com.aila.harvesttrack.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobAuditSessionDto {
    private Instant startTime;
    private Instant stopTime;      // null if not stopped yet
    private Long durationSeconds;  // null if stopTime is null
    private String startNote;
    private String stopNote;
}