package com.aila.harvesttrack.controller;

import com.aila.harvesttrack.dto.ApiResponse;
import com.aila.harvesttrack.model.Customer;
import com.aila.harvesttrack.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    // ── GET all customers
    // GET /api/customers?ownerId=owner123
    @GetMapping
    public ResponseEntity<ApiResponse<List<Customer>>> getAllCustomers(
            @RequestParam Integer ownerId) {
        try {
            List<Customer> customers = customerService.getAllCustomers(ownerId);
            return ResponseEntity.ok(
                    ApiResponse.success("Customers fetched successfully", customers)
            );
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ── GET customer by ID
    // GET /api/customers/1?ownerId=owner123
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Customer>> getCustomerById(
            @PathVariable int id,
            @RequestParam String ownerId) {
        try {
            Customer customer = customerService.getCustomerById(id, ownerId);
            return ResponseEntity.ok(
                    ApiResponse.success("Customer fetched successfully", customer)
            );
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ── POST add customer
    // POST /api/customers?ownerId=owner123
    @PostMapping
    public ResponseEntity<ApiResponse<Customer>> addCustomer(
            @RequestBody Customer customer,
            @RequestParam Integer ownerId) {
        try {
            Customer saved = customerService.addCustomer(customer, ownerId);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Customer added successfully", saved));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ── PUT update customer
    // PUT /api/customers/1?ownerId=owner123
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Customer>> updateCustomer(
            @PathVariable int id,
            @RequestBody Customer customer,
            @RequestParam String ownerId) {
        try {
            Customer updated = customerService.updateCustomer(id, customer, ownerId);
            return ResponseEntity.ok(
                    ApiResponse.success("Customer updated successfully", updated)
            );
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ── DELETE customer
    // DELETE /api/customers/1?ownerId=owner123
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(
            @PathVariable int id,
            @RequestParam String ownerId) {
        try {
            customerService.deleteCustomer(id, ownerId);
            return ResponseEntity.ok(
                    ApiResponse.success("Customer deleted successfully", null)
            );
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ── GET search customers
    // GET /api/customers/search?keyword=raju&ownerId=owner123
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Customer>>> searchCustomers(
            @RequestParam String keyword,
            @RequestParam String ownerId) {
        try {
            List<Customer> results = customerService.searchCustomers(keyword, ownerId);
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