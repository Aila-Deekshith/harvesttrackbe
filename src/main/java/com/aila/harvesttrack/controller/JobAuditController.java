package com.aila.harvesttrack.controller;

import com.aila.harvesttrack.dto.ApiResponse;
import com.aila.harvesttrack.dto.JobAuditDto;
import com.aila.harvesttrack.dto.JobAuditSessionDto;
import com.aila.harvesttrack.service.JobAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class JobAuditController {

    private final JobAuditService jobAuditService;

    // POST /api/jobs/{id}/audit/start
    @PostMapping("/{id}/audit/start")
    public ResponseEntity<ApiResponse<?>> recordStart(
            @PathVariable Integer id,
            @RequestBody(required = false) Map<String, String> body) {
        String note = (body != null) ? body.get("note") : null;
        JobAuditDto dto = jobAuditService.recordEvent(id, "START", null, note);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Start recorded", dto));
    }

    // POST /api/jobs/{id}/audit/stop
    @PostMapping("/{id}/audit/stop")
    public ResponseEntity<ApiResponse<?>> recordStop(
            @PathVariable Integer id,
            @RequestBody(required = false) Map<String, String> body) {
        String note = (body != null) ? body.get("note") : null;
        JobAuditDto dto = jobAuditService.recordEvent(id, "STOP", null, note);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Stop recorded", dto));
    }

    // GET /api/jobs/{id}/audit/raw
    @GetMapping("/{id}/audit")
    public ResponseEntity<ApiResponse<List<JobAuditDto>>> getRawAudits(@PathVariable Integer id) {
        List<JobAuditDto> audits = jobAuditService.getRawAuditsForJob(id);
        return ResponseEntity.ok(ApiResponse.success("Audits fetched", audits));
    }

    // GET /api/jobs/{id}/audit/sessions
    @GetMapping("/{id}/audit/sessions")
    public ResponseEntity<ApiResponse<List<JobAuditSessionDto>>> getSessions(@PathVariable Integer id) {
        List<JobAuditSessionDto> sessions = jobAuditService.getSessionsForJob(id);
        return ResponseEntity.ok(ApiResponse.success("Audit sessions fetched", sessions));
    }
}
