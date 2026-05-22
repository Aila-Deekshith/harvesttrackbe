package com.aila.harvesttrack.controller;

import com.aila.harvesttrack.dto.ActivityRequestDto;
import com.aila.harvesttrack.dto.ActivityResponseDto;
import com.aila.harvesttrack.dto.ApiResponse;
import com.aila.harvesttrack.model.Activity;
import com.aila.harvesttrack.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/activities")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ActivityResponseDto>>> getAll() {
        try {
            List<Activity> activities = activityService.getAllActivities();
            List<ActivityResponseDto> dtos = activities.stream()
                    .map(a -> new ActivityResponseDto(a.getId(),a.getActivityName(), a.getCropType()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Activities fetched successfully", dtos));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET /api/activities/1
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Activity>> getById(
            @PathVariable Integer id) {
        try {
            return ResponseEntity.ok(ApiResponse.success(
                    "Activity fetched successfully",
                    activityService.getActivityById(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // POST /api/activities
    @PostMapping
    public ResponseEntity<ApiResponse<Activity>> add(
            @RequestBody ActivityRequestDto dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(
                            "Activity added successfully",
                            activityService.addActivity(dto)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // PUT /api/activities/1
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Activity>> update(
            @PathVariable Integer id,
            @RequestBody  ActivityRequestDto dto) {
        try {
            return ResponseEntity.ok(ApiResponse.success(
                    "Activity updated successfully",
                    activityService.updateActivity(id, dto)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // DELETE /api/activities/1
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Integer id) {
        try {
            activityService.deleteActivity(id);
            return ResponseEntity.ok(
                    ApiResponse.success("Activity deleted successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}