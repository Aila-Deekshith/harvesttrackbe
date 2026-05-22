package com.aila.harvesttrack.service;

import com.aila.harvesttrack.dto.JobAuditDto;
import com.aila.harvesttrack.dto.JobAuditSessionDto;

import java.time.Instant;
import java.util.List;

public interface JobAuditService {
    JobAuditDto recordEvent(Integer jobId, String type, Instant eventTs, String note);
    List<JobAuditDto> getRawAuditsForJob(Integer jobId);
    List<JobAuditSessionDto> getSessionsForJob(Integer jobId);
}