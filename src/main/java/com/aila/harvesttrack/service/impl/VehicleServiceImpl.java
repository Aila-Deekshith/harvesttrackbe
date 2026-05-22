package com.aila.harvesttrack.service.impl;

import com.aila.harvesttrack.dto.VehicleRequestDto;
import com.aila.harvesttrack.model.Owner;
import com.aila.harvesttrack.model.Vehicle;
import com.aila.harvesttrack.repository.OwnerRepository;
import com.aila.harvesttrack.repository.VehicleRepository;
import com.aila.harvesttrack.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Override
    public List<Vehicle> getAllVehiclesByOwner(Integer ownerId) {
        ownerRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException(
                        "Owner not found with id: " + ownerId));
        return vehicleRepository.findByOwnerIdAndDeletedAtIsNull(ownerId);
    }

    @Override
    public Vehicle getVehicleById(Integer id) {
        return (Vehicle) vehicleRepository.findByIdAndDeletedAtIsNull(id)  // use correct method
                .orElseThrow(() -> new RuntimeException(
                        "Vehicle not found with id: " + id));
    }

    @Override
    public Vehicle getVehicleByRegistrationNumber(String registrationNumber) {
        return vehicleRepository
                .findByRegistrationNumberAndDeletedAtIsNull(registrationNumber)
                .orElseThrow(() -> new RuntimeException(
                        "Vehicle not found with registration: " + registrationNumber));
    }

    @Override
    public List<Vehicle> getVehiclesByType(Integer ownerId, String type) {
        return vehicleRepository
                .findByTypeAndOwnerIdAndDeletedAtIsNull(type, ownerId);
    }

    @Override
    public List<Vehicle> getVehiclesByFuelType(Integer ownerId, String fuelType) {
        return vehicleRepository
                .findByFuelTypeAndOwnerIdAndDeletedAtIsNull(fuelType, ownerId);
    }

    @Override
    public List<Vehicle> searchVehicles(String keyword, Integer ownerId) {
        return vehicleRepository
                .findByOwnerIdAndDeletedAtIsNullAndNameContainingIgnoreCaseOrOwnerIdAndDeletedAtIsNullAndRegistrationNumberContainingIgnoreCase(
                        ownerId, keyword, ownerId, keyword);
    }

    @Override
    public Vehicle addVehicle(VehicleRequestDto dto) {

        Owner owner = ownerRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new RuntimeException(
                        "Owner not found with id: " + dto.getOwnerId()));

        if (vehicleRepository.existsByRegistrationNumberAndDeletedAtIsNull(
                dto.getRegistrationNumber())) {
            throw new RuntimeException(
                    "Vehicle with registration number "
                            + dto.getRegistrationNumber() + " already exists");
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setName(dto.getName());
        vehicle.setType(dto.getType());
        vehicle.setRegistrationNumber(dto.getRegistrationNumber());
        vehicle.setFuelType(dto.getFuelType());
        vehicle.setPurchaseDate(dto.getPurchaseDate());
        vehicle.setOwner(owner);

        return vehicleRepository.save(vehicle);
    }

    @Override
    public Vehicle updateVehicle(Integer id, VehicleRequestDto dto) {

        Vehicle existing = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Vehicle not found with id: " + id));

        if (dto.getRegistrationNumber() != null &&
                !dto.getRegistrationNumber().equals(existing.getRegistrationNumber()) &&
                vehicleRepository.existsByRegistrationNumberAndIdNotAndDeletedAtIsNull(
                        dto.getRegistrationNumber(), id)) {
            throw new RuntimeException(
                    "Registration number " + dto.getRegistrationNumber()
                            + " already exists");
        }

        if (dto.getName()               != null) existing.setName(dto.getName());
        if (dto.getType()               != null) existing.setType(dto.getType());
        if (dto.getRegistrationNumber() != null) existing.setRegistrationNumber(dto.getRegistrationNumber());
        if (dto.getFuelType()           != null) existing.setFuelType(dto.getFuelType());
        if (dto.getPurchaseDate()       != null) existing.setPurchaseDate(dto.getPurchaseDate());

        return vehicleRepository.save(existing);
    }

    @Override
    public void deleteVehicle(Integer id) {
        vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Vehicle not found with id: " + id));
        vehicleRepository.softDeleteById(id);
    }

    @Override
    public List<Vehicle> getVehiclesByOwner(Integer ownerId) {
        return vehicleRepository.findByOwnerIdAndDeletedAtIsNull(ownerId);
    }
}