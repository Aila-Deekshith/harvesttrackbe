package com.aila.harvesttrack.service.impl;

import com.aila.harvesttrack.dto.JobsRequestDto;
import com.aila.harvesttrack.model.Activity;
import com.aila.harvesttrack.model.Customer;
import com.aila.harvesttrack.model.Jobs;
import com.aila.harvesttrack.model.Vehicle;
import com.aila.harvesttrack.repository.ActivityRepository;
import com.aila.harvesttrack.repository.CustomerRepository;
import com.aila.harvesttrack.repository.JobsRepository;
import com.aila.harvesttrack.repository.VehicleRepository;
import com.aila.harvesttrack.service.JobsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobsServiceImpl implements JobsService {

    private final JobsRepository jobsRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final ActivityRepository activityRepository;

    // ── Get all jobs
    @Override
    public List<Jobs> getAllJobs() {
        return jobsRepository.findByDeletedAtIsNull();
    }

    // ── Get job by ID
    @Override
    public Jobs getJobById(Integer id) {
        return jobsRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException(
                        "Job not found with id: " + id
                ));
    }

    // ── Get jobs by owner
    @Override
    public List<Jobs> getJobsByOwner(Integer ownerId) {
        return jobsRepository.findByOwnerIdAndDeletedAtIsNull(ownerId);
    }

    // ── Get jobs by customer
    @Override
    public List<Jobs> getJobsByCustomer(Integer customerId) {

        // Verify customer exists
        customerRepository
                .findById(customerId)
                .orElseThrow(() -> new RuntimeException(
                        "Customer not found with id: " + customerId
                ));

        return jobsRepository.findByCustomerIdAndDeletedAtIsNull(customerId);
    }

    // ── Get jobs by vehicle
    @Override
    public List<Jobs> getJobsByVehicle(Integer vehicleId) {

        // Verify vehicle exists
        vehicleRepository
                .findById(vehicleId)
                .orElseThrow(() -> new RuntimeException(
                        "Vehicle not found with id: " + vehicleId
                ));

        return jobsRepository.findByVehicleIdAndDeletedAtIsNull(vehicleId);
    }

    // ── Get jobs by status
    @Override
    public List<Jobs> getJobsByStatus(String status) {
        return jobsRepository.findByStatusAndDeletedAtIsNull(status);
    }

    // ── Get jobs by owner and status
    @Override
    public List<Jobs> getJobsByOwnerAndStatus(Integer ownerId, String status) {
        return jobsRepository.findByOwnerIdAndStatus(ownerId, status);
    }

    // ── Add job
    @Override
    public Jobs addJob(JobsRequestDto dto) {

        // Fetch customer
        Customer customer = customerRepository
                .findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException(
                        "Customer not found with id: " + dto.getCustomerId()
                ));

        // Fetch vehicle
        Vehicle vehicle = vehicleRepository
                .findById(dto.getVehicleId())
                .orElseThrow(() -> new RuntimeException(
                        "Vehicle not found with id: " + dto.getVehicleId()
                ));

        //Fetch activity
        Activity activity = activityRepository.findById(dto.getActivityId())
                .orElseThrow(() -> new RuntimeException(
                        "Activity not found with id: " + dto.getActivityId()
                ));

        Jobs job = new Jobs();
        job.setCustomer(customer);
        job.setVehicle(vehicle);
        job.setDescription(dto.getDescription());
        job.setStatus(dto.getStatus() != null
                ? dto.getStatus()
                : "PENDING"
        );
        job.setStartDate(dto.getStartDate());
        job.setEndDate(dto.getEndDate());
        job.setActivity(activity);
        job.setAcres(dto.getAcres());
        job.setCost(dto.getCost());
        return jobsRepository.save(job);
    }

    // ── Update job
    @Override
    public Jobs updateJob(Integer id, JobsRequestDto dto) {

        Jobs existing = jobsRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException(
                        "Job not found with id: " + id
                ));

        // Update customer if provided
        if (dto.getCustomerId() != null) {
            Customer customer = customerRepository
                    .findById(dto.getCustomerId())
                    .orElseThrow(() -> new RuntimeException(
                            "Customer not found with id: " + dto.getCustomerId()
                    ));
            existing.setCustomer(customer);
        }

        // Update vehicle if provided
        if (dto.getVehicleId() != null) {
            Vehicle vehicle = vehicleRepository
                    .findById(dto.getVehicleId())
                    .orElseThrow(() -> new RuntimeException(
                            "Vehicle not found with id: " + dto.getVehicleId()
                    ));
            existing.setVehicle(vehicle);
        }

        if (dto.getDescription() != null)
            existing.setDescription(dto.getDescription());

        if (dto.getStatus() != null)
            existing.setStatus(dto.getStatus());

        if (dto.getStartDate() != null)
            existing.setStartDate(dto.getStartDate());

        if (dto.getEndDate() != null)
            existing.setEndDate(dto.getEndDate());

        return jobsRepository.save(existing);
    }

    // ── Update job status only
    @Override
    public Jobs updateJobStatus(Integer id, String status) {

        Jobs existing = jobsRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException(
                        "Job not found with id: " + id
                ));

        existing.setStatus(status);

        // Auto set endDate when job completed
        if (status.equalsIgnoreCase("COMPLETED")) {
            existing.setEndDate(Instant.now());
        }

        return jobsRepository.save(existing);
    }

    // ── Soft delete job
    @Override
    public void deleteJob(Integer id) {

        Jobs existing = jobsRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException(
                        "Job not found with id: " + id
                ));

        existing.setDeletedAt(Instant.now());
        jobsRepository.save(existing);
    }

    // ── Hard delete job
    @Override
    public void hardDeleteJob(Integer id) {

        Jobs existing = jobsRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Job not found with id: " + id
                ));

        jobsRepository.delete(existing);
    }
}