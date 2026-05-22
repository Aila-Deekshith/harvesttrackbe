package com.aila.harvesttrack.repository;

import com.aila.harvesttrack.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Integer> {

    // ── Find owner by phone
    Optional<Owner> findByPhone(String phone);

    // ── Check if phone already exists
    boolean existsByPhone(String phone);

    // ── Search by name or phone
    List<Owner> findByNameContainingIgnoreCaseOrPhoneContaining(
            String name, String phone
    );
}