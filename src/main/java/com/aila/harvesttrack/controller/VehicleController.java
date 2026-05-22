package com.aila.harvesttrack.controller;

import com.aila.harvesttrack.dto.ApiResponse;
import com.aila.harvesttrack.dto.VehicleRequestDto;
import com.aila.harvesttrack.model.Vehicle;
import com.aila.harvesttrack.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "*")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    // GET /api/vehicles/owner/1
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<ApiResponse<List<Vehicle>>> getAllByOwner(
            @PathVariable Integer ownerId) {
        try {
            return ResponseEntity.ok(ApiResponse.success(
                    "Vehicles fetched successfully",
                    vehicleService.getAllVehiclesByOwner(ownerId)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET /api/vehicles/1
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Vehicle>> getById(
            @PathVariable Integer id) {
        try {
            return ResponseEntity.ok(ApiResponse.success(
                    "Vehicle fetched successfully",
                    vehicleService.getVehicleById(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET /api/vehicles/registration/TS01AB1234
    @GetMapping("/registration/{regNumber}")
    public ResponseEntity<ApiResponse<Vehicle>> getByRegistration(
            @PathVariable String regNumber) {
        try {
            return ResponseEntity.ok(ApiResponse.success(
                    "Vehicle fetched successfully",
                    vehicleService.getVehicleByRegistrationNumber(regNumber)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET /api/vehicles/owner/1/type/HARVESTER
    @GetMapping("/owner/{ownerId}/type/{type}")
    public ResponseEntity<ApiResponse<List<Vehicle>>> getByType(
            @PathVariable Integer ownerId,
            @PathVariable String  type) {
        try {
            return ResponseEntity.ok(ApiResponse.success(
                    "Vehicles fetched successfully",
                    vehicleService.getVehiclesByType(ownerId, type)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET /api/vehicles/owner/1/fuel/DIESEL
    @GetMapping("/owner/{ownerId}/fuel/{fuelType}")
    public ResponseEntity<ApiResponse<List<Vehicle>>> getByFuelType(
            @PathVariable Integer ownerId,
            @PathVariable String  fuelType) {
        try {
            return ResponseEntity.ok(ApiResponse.success(
                    "Vehicles fetched successfully",
                    vehicleService.getVehiclesByFuelType(ownerId, fuelType)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET /api/vehicles/search?keyword=john&ownerId=1
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Vehicle>>> search(
            @RequestParam String  keyword,
            @RequestParam Integer ownerId) {
        try {
            return ResponseEntity.ok(ApiResponse.success(
                    "Search results fetched",
                    vehicleService.searchVehicles(keyword, ownerId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // POST /api/vehicles
    @PostMapping
    public ResponseEntity<ApiResponse<Vehicle>> add(
            @RequestBody VehicleRequestDto dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(
                            "Vehicle added successfully",
                            vehicleService.addVehicle(dto)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // PUT /api/vehicles/1
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Vehicle>> update(
            @PathVariable Integer id,
            @RequestBody  VehicleRequestDto dto) {
        try {
            return ResponseEntity.ok(ApiResponse.success(
                    "Vehicle updated successfully",
                    vehicleService.updateVehicle(id, dto)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // DELETE /api/vehicles/1
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Integer id) {
        try {
            vehicleService.deleteVehicle(id);
            return ResponseEntity.ok(
                    ApiResponse.success("Vehicle deleted successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}