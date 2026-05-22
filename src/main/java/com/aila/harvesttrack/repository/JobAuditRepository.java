package com.aila.harvesttrack.repository;

import com.aila.harvesttrack.model.JobAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobAuditRepository extends JpaRepository<JobAudit, Long> {
    List<JobAudit> findByJobIdOrderByEventTimestampAsc(Integer jobId); // if Jobs.id is Integer
}