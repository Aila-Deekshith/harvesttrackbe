package com.aila.harvesttrack.service.impl;

import com.aila.harvesttrack.dto.ApiResponse;
import com.aila.harvesttrack.dto.JobsRequestDto;
import com.aila.harvesttrack.dto.JobsResponseDto;
import com.aila.harvesttrack.model.*;
import com.aila.harvesttrack.repository.*;
import com.aila.harvesttrack.service.JobsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobsServiceImpl implements JobsService {

    private final JobsRepository jobsRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final ActivityRepository activityRepository;
    private final OwnerRepository ownerRepository;

    // ── Get all jobs
    @Override
    public List<JobsResponseDto> getAllJobs() {
        return returnAllJobsResponseDto(jobsRepository.findByDeletedAtIsNull());
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
    public List<JobsResponseDto> getJobsByOwner(Integer ownerId) {
        return returnAllJobsResponseDto(jobsRepository.findByOwner_IdAndDeletedAtIsNull(ownerId));
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
        return jobsRepository.findByOwner_IdAndStatus(ownerId, status);
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

        //Fetch owner
        Owner owner = ownerRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new RuntimeException(
                        "Owner not found with id: " + dto.getOwnerId()
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
        job.setOwner(owner);
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

    @Override
    public List<JobsResponseDto> getRecentJobsForOwner(Integer ownerId) {
        return returnAllJobsResponseDto(jobsRepository.findByOwner_IdAndCreatedAtAfterAndDeletedAtIsNull(
                ownerId, Instant.now().minusSeconds( 24 * 3600)
        ));
    }

    public List<JobsResponseDto> returnAllJobsResponseDto(List<Jobs> jobs) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a"); // e.g. 08:30 AM
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // e.g. 2024-01-15
        ZoneId zone = ZoneId.systemDefault();

        List<JobsResponseDto> dtos = jobs.stream().map(j -> {
            JobsResponseDto dto = new JobsResponseDto();

            dto.setId(j.getId());

            // customerName and village (assume village stored in customer.address)
            if (j.getCustomer() != null) {
                dto.setCustomerName(j.getCustomer().getName());
                dto.setVillage(j.getCustomer().getAddress()); // adjust if you have a dedicated village field
            } else {
                dto.setCustomerName(null);
                dto.setVillage(null);
            }

            // crop from activity
            if (j.getActivity() != null) {
                dto.setCrop(j.getActivity().getCropType());
            } else {
                dto.setCrop(null);
            }

            // acres and amount (cost)
            Float acres = j.getAcres();
            Float cost = j.getCost();

            dto.setAcres(acres != null ? String.valueOf(acres) : null);

            if(cost != null){
                dto.setRate(cost.toString());
            } else {
                dto.setRate(null);
            }

            // startTime, endTime, date and duration (seconds)
            if (j.getStartDate() != null) {
                ZonedDateTime zStart = ZonedDateTime.ofInstant(j.getStartDate(), zone);
                dto.setStartTime(timeFormatter.format(zStart));
                dto.setDate(dateFormatter.format(zStart));
            } else {
                dto.setStartTime(null);
            }

            if (j.getEndDate() != null) {
                ZonedDateTime zEnd = ZonedDateTime.ofInstant(j.getEndDate(), zone);
                dto.setEndTime(timeFormatter.format(zEnd));

                // duration in seconds (end - start)
                if (j.getStartDate() != null) {
                    long seconds = Duration.between(j.getStartDate(), j.getEndDate()).getSeconds();
                    dto.setDuration(String.valueOf(seconds));
                    if(cost != null) {
                        dto.setAmount(String.valueOf(cost * ((float) seconds / (60 * 60))));
                    }
                } else {
                    dto.setDuration(null);
                    dto.setAmount(null);
                }
            } else {
                dto.setEndTime(null);
                dto.setDuration(null);
            }

            dto.setStatus(j.getStatus());
            dto.setNotes(j.getDescription());

            return dto;
        }).collect(Collectors.toList());

        return dtos;
    }
}