package com.aila.harvesttrack.service.impl;

import com.aila.harvesttrack.dto.CustomerResponseDTO;
import com.aila.harvesttrack.model.Customer;
import com.aila.harvesttrack.model.Jobs;
import com.aila.harvesttrack.model.Owner;
import com.aila.harvesttrack.repository.CustomerRepository;
import com.aila.harvesttrack.repository.OwnerRepository;
import com.aila.harvesttrack.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final OwnerRepository ownerRepository;
    private final DashboardServiceImpl dashboardService;

    // ── Get all customers of an owner
    @Override
    public List<CustomerResponseDTO> getAllCustomers(Integer ownerId) {
        List<Customer> customers = customerRepository.findByOwnerIdAndDeletedAtIsNullOrderByUpdatedAtDesc(ownerId);
        return customers.stream().map(customer -> {
            CustomerResponseDTO dto = new CustomerResponseDTO();
            dto.setId(customer.getId());
            dto.setName(customer.getName());
            dto.setPhone(customer.getPhone());
            dto.setAddress(customer.getAddress());
            dto.setCreatedAt(customer.getCreatedAt());
            dto.setUpdatedAt(customer.getUpdatedAt());
            dto.setJobsCount(customer.getJobs().size());
            dto.setAcres(customer.getJobs().stream()
                    .map(job -> job.getAcres() != null ? job.getAcres() : 0)
                    .reduce( 0f, Float::sum));

            dto.setAmount(customer.getJobs().stream()
                    .filter(job -> job.getStatus().equalsIgnoreCase("finished"))
                    .map(dashboardService::calculateJobAmount)
                    .reduce(0f, Float::sum));
            return dto;
        }).toList();
    }

    // ── Get customer by ID
    @Override
    public Customer getCustomerById(int id, Integer ownerId) {
        return customerRepository
                .findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new RuntimeException(
                        "Customer not found with id: " + id
                ));
    }

    // ── Add customer
    @Override
    public Customer addCustomer(Customer customer, Integer ownerId) {

        // Find owner
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        // Check duplicate phone for same owner
        if (customerRepository.existsByPhoneAndOwnerId(
                customer.getPhone(), ownerId)) {
            throw new RuntimeException(
                    "Customer with phone " + customer.getPhone() + " already exists"
            );
        }

        customer.setOwner(owner);
        return customerRepository.save(customer);
    }

    // ── Update customer
    @Override
    public Customer updateCustomer(int id, Customer updatedCustomer, Integer ownerId) {

        Customer existing = customerRepository
                .findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new RuntimeException(
                        "Customer not found with id: " + id
                ));

        existing.setName(updatedCustomer.getName());
        existing.setPhone(updatedCustomer.getPhone());
        existing.setAddress(updatedCustomer.getAddress());

        return customerRepository.save(existing);
    }

    // ── Delete customer
    @Override
    public void deleteCustomer(int id, Integer ownerId) {

        Customer existing = customerRepository
                .findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new RuntimeException(
                        "Customer not found with id: " + id
                ));

        existing.setDeletedAt(Instant.now());
        customerRepository.save(existing);
    }

    // ── Search customers
    @Override
    public List<Customer> searchCustomers(String keyword, Integer ownerId) {
        return customerRepository
                .findByOwnerIdAndNameContainingIgnoreCaseOrOwnerIdAndPhoneContaining(
                        ownerId, keyword,
                        ownerId, keyword
                );
    }
}