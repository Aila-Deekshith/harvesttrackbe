package com.aila.harvesttrack.service;

import com.aila.harvesttrack.dto.JobsRequestDto;
import com.aila.harvesttrack.dto.JobsResponseDto;
import com.aila.harvesttrack.model.Jobs;

import java.util.List;

public interface JobsService {

    // ── Get all jobs
    List<JobsResponseDto> getAllJobs();

    // ── Get job by ID
    Jobs getJobById(Integer id);

    // ── Get all jobs by owner
    List<JobsResponseDto> getJobsByOwner(Integer ownerId);

    // ── Get jobs by customer
    List<Jobs> getJobsByCustomer(Integer customerId);

    // ── Get jobs by vehicle
    List<Jobs> getJobsByVehicle(Integer vehicleId);

    // ── Get jobs by status
    List<Jobs> getJobsByStatus(String status);

    // ── Get jobs by owner and status
    List<Jobs> getJobsByOwnerAndStatus(Integer ownerId, String status);

    // ── Add new job
    Jobs addJob(JobsRequestDto jobsRequestDto);

    // ── Update job
    Jobs updateJob(Integer id, JobsRequestDto jobsRequestDto);

    // ── Update job status
    Jobs updateJobStatus(Integer id, String status);

    // ── Soft delete job
    void deleteJob(Integer id);

    // ── Hard delete job
    void hardDeleteJob(Integer id);

    List<JobsResponseDto> getRecentJobsForOwner(Integer ownerId);
}