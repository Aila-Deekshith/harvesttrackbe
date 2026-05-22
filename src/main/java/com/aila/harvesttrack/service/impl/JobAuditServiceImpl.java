package com.aila.harvesttrack.service.impl;

import com.aila.harvesttrack.dto.JobAuditDto;
import com.aila.harvesttrack.dto.JobAuditSessionDto;
import com.aila.harvesttrack.model.JobAudit;
import com.aila.harvesttrack.model.Jobs;
import com.aila.harvesttrack.repository.JobAuditRepository;
import com.aila.harvesttrack.repository.JobsRepository;
import com.aila.harvesttrack.service.JobAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobAuditServiceImpl implements JobAuditService {

    private final JobAuditRepository auditRepository;
    private final JobsRepository jobsRepository;

    @Override
    @Transactional
    public JobAuditDto recordEvent(Integer jobId, String type, Instant eventTs, String note) {
        Jobs job = jobsRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found: " + jobId));

        JobAudit audit = new JobAudit();
        audit.setJob(job);
        audit.setActivityType(type);
        audit.setEventTimestamp(eventTs != null ? eventTs : Instant.now());
        audit.setNote(note);
        JobAudit saved = auditRepository.save(audit);

        return new JobAuditDto(saved.getId(), job.getId(), saved.getActivityType(), saved.getEventTimestamp(), saved.getNote());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobAuditDto> getRawAuditsForJob(Integer jobId) {
        List<JobAudit> audits = auditRepository.findByJobIdOrderByEventTimestampAsc(jobId);
        List<JobAuditDto> dtos = new ArrayList<>();
        for (JobAudit a : audits) {
            dtos.add(new JobAuditDto(a.getId(), a.getJob().getId(), a.getActivityType(), a.getEventTimestamp(), a.getNote()));
        }
        return dtos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobAuditSessionDto> getSessionsForJob(Integer jobId) {
        List<JobAudit> audits = auditRepository.findByJobIdOrderByEventTimestampAsc(jobId);
        List<JobAuditSessionDto> sessions = new ArrayList<>();

        Instant pendingStart = null;
        String pendingStartNote = null;

        for (JobAudit a : audits) {
            if (a.getActivityType().equalsIgnoreCase("START")) {
                // start a new pending session; if there was already a pendingStart,
                // choose to overwrite or create nested sessions -> here we overwrite (last start)
                pendingStart = a.getEventTimestamp();
                pendingStartNote = a.getNote();
            } else if (a.getActivityType().equalsIgnoreCase("STOP")) {
                if (pendingStart != null) {
                    Instant stop = a.getEventTimestamp();
                    long duration = Duration.between(pendingStart, stop).getSeconds();
                    sessions.add(new JobAuditSessionDto(pendingStart, stop, duration, pendingStartNote, a.getNote()));
                    pendingStart = null;
                    pendingStartNote = null;
                } else {
                    // STOP without prior START: treat as zero-length or create session with null start
                    sessions.add(new JobAuditSessionDto(null, a.getEventTimestamp(), null, null, a.getNote()));
                }
            }
        }

        // if there's an open start (no stop yet), add a session with null stop
        if (pendingStart != null) {
            sessions.add(new JobAuditSessionDto(pendingStart, null, null, pendingStartNote, null));
        }

        return sessions;
    }
}