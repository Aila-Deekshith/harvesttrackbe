package com.aila.harvesttrack.service.impl;

import com.aila.harvesttrack.model.Owner;
import com.aila.harvesttrack.repository.OwnerRepository;
import com.aila.harvesttrack.service.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository ownerRepository;

    // ── Get all owners
    @Override
    public List<Owner> getAllOwners() {
        return ownerRepository.findAll();
    }

    // ── Get owner by ID
    @Override
    public Owner getOwnerById(int id) {
        return ownerRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Owner not found with id: " + id
                ));
    }

    // ── Get owner by phone
    @Override
    public Owner getOwnerByPhone(String phone) {
        return ownerRepository
                .findByPhone(phone)
                .orElseThrow(() -> new RuntimeException(
                        "Owner not found with phone: " + phone
                ));
    }

    // ── Add owner
    @Override
    public Owner addOwner(Owner owner) {

        // Check duplicate phone
        if (ownerRepository.existsByPhone(owner.getPhone())) {
            throw new RuntimeException(
                    "Owner with phone " + owner.getPhone() + " already exists"
            );
        }

        return ownerRepository.save(owner);
    }

    // ── Update owner
    @Override
    public Owner updateOwner(int id, Owner updatedOwner) {

        Owner existing = ownerRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Owner not found with id: " + id
                ));

        // Check if new phone belongs to another owner
        if (!existing.getPhone().equals(updatedOwner.getPhone()) &&
                ownerRepository.existsByPhone(updatedOwner.getPhone())) {
            throw new RuntimeException(
                    "Phone " + updatedOwner.getPhone() + " is already registered"
            );
        }

        existing.setName(updatedOwner.getName());
        existing.setPhone(updatedOwner.getPhone());

        return ownerRepository.save(existing);
    }

    // ── Delete owner
    @Override
    public void deleteOwner(int id) {

        Owner existing = ownerRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Owner not found with id: " + id
                ));

        existing.setDeletedAt(Instant.now());
        ownerRepository.save(existing);
    }

    // ── Search owners
    @Override
    public List<Owner> searchOwners(String keyword) {
        return ownerRepository
                .findByNameContainingIgnoreCaseOrPhoneContaining(
                        keyword, keyword
                );
    }
}