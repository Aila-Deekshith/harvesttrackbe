package com.aila.harvesttrack.service;

import com.aila.harvesttrack.model.Owner;

import java.util.List;

public interface OwnerService {

    // ── Get all owners
    List<Owner> getAllOwners();

    // ── Get owner by ID
    Owner getOwnerById(int id);

    // ── Get owner by phone
    Owner getOwnerByPhone(String phone);

    // ── Add new owner
    Owner addOwner(Owner owner);

    // ── Update owner
    Owner updateOwner(int id, Owner owner);

    // ── Delete owner
    void deleteOwner(int id);

    // ── Search owners
    List<Owner> searchOwners(String keyword);
}