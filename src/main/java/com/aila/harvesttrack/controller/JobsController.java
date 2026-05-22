package com.aila.harvesttrack.controller;

import com.aila.harvesttrack.dto.ApiResponse;
import com.aila.harvesttrack.dto.JobsRequestDto;
import com.aila.harvesttrack.dto.JobsResponseDto;
import com.aila.harvesttrack.model.Jobs;
import com.aila.harvesttrack.service.JobsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class JobsController {

    private final JobsService jobsService;

    // ── GET all jobs
    // GET /api/jobs
    @GetMapping
    public ResponseEntity<ApiResponse<List<JobsResponseDto>>> getAllJobs() {
        try {
            List<Jobs> jobs = jobsService.getAllJobs();

            // formatters
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

            return ResponseEntity.ok(ApiResponse.success("Jobs fetched successfully", dtos));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ── GET job by ID
    // GET /api/jobs/1
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Jobs>> getJobById(
            @PathVariable Integer id) {
        try {
            Jobs job = jobsService.getJobById(id);
            return ResponseEntity.ok(
                    ApiResponse.success("Job fetched successfully", job)
            );
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ── GET jobs by owner
    // GET /api/jobs/owner/1
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<ApiResponse<List<Jobs>>> getJobsByOwner(
            @PathVariable Integer ownerId) {
        try {
            List<Jobs> jobs = jobsService.getJobsByOwner(ownerId);
            return ResponseEntity.ok(
                    ApiResponse.success("Jobs fetched successfully", jobs)
            );
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ── GET jobs by customer
    // GET /api/jobs/customer/1
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<Jobs>>> getJobsByCustomer(
            @PathVariable Integer customerId) {
        try {
            List<Jobs> jobs = jobsService.getJobsByCustomer(customerId);
            return ResponseEntity.ok(
                    ApiResponse.success("Jobs fetched successfully", jobs)
            );
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ── GET jobs by vehicle
    // GET /api/jobs/vehicle/1
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<ApiResponse<List<Jobs>>> getJobsByVehicle(
            @PathVariable Integer vehicleId) {
        try {
            List<Jobs> jobs = jobsService.getJobsByVehicle(vehicleId);
            return ResponseEntity.ok(
                    ApiResponse.success("Jobs fetched successfully", jobs)
            );
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ── GET jobs by status
    // GET /api/jobs/status/PENDING
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<Jobs>>> getJobsByStatus(
            @PathVariable String status) {
        try {
            List<Jobs> jobs = jobsService.getJobsByStatus(status);
            return ResponseEntity.ok(
                    ApiResponse.success("Jobs fetched successfully", jobs)
            );
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ── GET jobs by owner and status
    // GET /api/jobs/owner/1/status/COMPLETED
    @GetMapping("/owner/{ownerId}/status/{status}")
    public ResponseEntity<ApiResponse<List<Jobs>>> getJobsByOwnerAndStatus(
            @PathVariable Integer ownerId,
            @PathVariable String  status) {
        try {
            List<Jobs> jobs = jobsService.getJobsByOwnerAndStatus(ownerId, status);
            return ResponseEntity.ok(
                    ApiResponse.success("Jobs fetched successfully", jobs)
            );
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ── POST add job
    // POST /api/jobs
    @PostMapping
    public ResponseEntity<ApiResponse<Jobs>> addJob(
            @RequestBody JobsRequestDto dto) {
        try {
            Jobs saved = jobsService.addJob(dto);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Job created successfully", saved));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ── PUT update job
    // PUT /api/jobs/1
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Jobs>> updateJob(
            @PathVariable Integer id,
            @RequestBody  JobsRequestDto dto) {
        try {
            Jobs updated = jobsService.updateJob(id, dto);
            return ResponseEntity.ok(
                    ApiResponse.success("Job updated successfully", updated)
            );
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ── PATCH update job status only
    // PATCH /api/jobs/1/status
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Jobs>> updateJobStatus(
            @PathVariable Integer id,
            @RequestBody  Map<String, String> body) {
        try {
            String status  = body.get("status");
            if (status == null || status.isBlank()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Status is required"));
            }
            Jobs updated = jobsService.updateJobStatus(id, status);
            return ResponseEntity.ok(
                    ApiResponse.success("Job status updated successfully", updated)
            );
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ── DELETE soft delete job
    // DELETE /api/jobs/1
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteJob(
            @PathVariable Integer id) {
        try {
            jobsService.deleteJob(id);
            return ResponseEntity.ok(
                    ApiResponse.success("Job deleted successfully", null)
            );
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ── DELETE hard delete job
    // DELETE /api/jobs/1/hard
    @DeleteMapping("/{id}/hard")
    public ResponseEntity<ApiResponse<Void>> hardDeleteJob(
            @PathVariable Integer id) {
        try {
            jobsService.hardDeleteJob(id);
            return ResponseEntity.ok(
                    ApiResponse.success("Job permanently deleted", null)
            );
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}