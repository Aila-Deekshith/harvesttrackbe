package com.aila.harvesttrack.controller;

import com.aila.harvesttrack.dto.ApiResponse;
import com.aila.harvesttrack.model.Owner;
import com.aila.harvesttrack.service.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owners")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;

    // ── GET all owners
    // GET /api/owners
    @GetMapping
    public ResponseEntity<ApiResponse<List<Owner>>> getAllOwners() {
        try {
            List<Owner> owners = ownerService.getAllOwners();
            return ResponseEntity.ok(
                    ApiResponse.success("Owners fetched successfully", owners)
            );
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ── GET owner by ID
    // GET /api/owners/1
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Owner>> getOwnerById(
            @PathVariable int id) {
        try {
            Owner owner = ownerService.getOwnerById(id);
            return ResponseEntity.ok(
                    ApiResponse.success("Owner fetched successfully", owner)
            );
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ── GET owner by phone
    // GET /api/owners/phone/9876543210
    @GetMapping("/phone/{phone}")
    public ResponseEntity<ApiResponse<Owner>> getOwnerByPhone(
            @PathVariable String phone) {
        try {
            Owner owner = ownerService.getOwnerByPhone(phone);
            return ResponseEntity.ok(
                    ApiResponse.success("Owner fetched successfully", owner)
            );
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ── POST add owner
    // POST /api/owners
    @PostMapping
    public ResponseEntity<ApiResponse<Owner>> addOwner(
            @RequestBody Owner owner) {
        try {
            Owner saved = ownerService.addOwner(owner);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Owner added successfully", saved));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ── PUT update owner
    // PUT /api/owners/1
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Owner>> updateOwner(
            @PathVariable int id,
            @RequestBody Owner owner) {
        try {
            Owner updated = ownerService.updateOwner(id, owner);
            return ResponseEntity.ok(
                    ApiResponse.success("Owner updated successfully", updated)
            );
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ── DELETE owner
    // DELETE /api/owners/1
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOwner(
            @PathVariable int id) {
        try {
            ownerService.deleteOwner(id);
            return ResponseEntity.ok(
                    ApiResponse.success("Owner deleted successfully", null)
            );
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ── GET search owners
    // GET /api/owners/search?keyword=raju
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Owner>>> searchOwners(
            @RequestParam String keyword) {
        try {
            List<Owner> results = ownerService.searchOwners(keyword);
            return ResponseEntity.ok(
                    ApiResponse.success("Search results fetched", results)
            );
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}