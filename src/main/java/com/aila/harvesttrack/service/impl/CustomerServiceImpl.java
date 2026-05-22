package com.aila.harvesttrack.service.impl;

import com.aila.harvesttrack.model.Customer;
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
    private final    OwnerRepository ownerRepository;

    // ── Get all customers of an owner
    @Override
    public List<Customer> getAllCustomers(Integer ownerId) {
        return customerRepository.findByOwnerIdAndDeletedAtIsNullOrderByUpdatedAtDesc(ownerId);
    }

    // ── Get customer by ID
    @Override
    public Customer getCustomerById(int id, String ownerId) {
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
    public Customer updateCustomer(int id, Customer updatedCustomer, String ownerId) {

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
    public void deleteCustomer(int id, String ownerId) {

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
    public List<Customer> searchCustomers(String keyword, String ownerId) {
        return customerRepository
                .findByOwnerIdAndNameContainingIgnoreCaseOrOwnerIdAndPhoneContaining(
                        ownerId, keyword,
                        ownerId, keyword
                );
    }
}