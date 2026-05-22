package com.aila.harvesttrack.repository;

import com.aila.harvesttrack.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    // ── Find all customers by owner
    List<Customer> findByOwnerId(Integer ownerId);

    // ── Find customer by id and owner
    Optional<Customer> findByIdAndOwnerId(int id, Integer ownerId);

    // ── Check if phone exists for owner
    boolean existsByPhoneAndOwnerId(String phone, Integer ownerId);

    // ── Search by name or phone
    List<Customer> findByOwnerIdAndNameContainingIgnoreCaseOrOwnerIdAndPhoneContaining(
            Integer ownerId1, String name,
            Integer ownerId2, String phone
    );

    List<Customer> findByOwnerIdAndDeletedAtIsNullOrderByUpdatedAtDesc(Integer ownerId);
}