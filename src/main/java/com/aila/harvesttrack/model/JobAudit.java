package com.aila.harvesttrack.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "job_audit", indexes = {
        @Index(name = "idx_job_audit_job_id_ts", columnList = "job_id, event_ts")
})
public class JobAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // reference to Jobs.id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Jobs job;

    @Column(name = "activity_type", nullable = false, length = 20)
    private String activityType; // START or STOP

    @Column(name = "event_ts", nullable = false)
    private Instant eventTimestamp; // time of start or stop event (UTC recommended)

    @Column(name = "note", length = 1024)
    private String note;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}