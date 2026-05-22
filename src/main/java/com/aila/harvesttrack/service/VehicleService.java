package com.aila.harvesttrack.service;

import com.aila.harvesttrack.dto.VehicleRequestDto;
import com.aila.harvesttrack.model.Vehicle;

import java.util.List;

public interface VehicleService {

    List<Vehicle> getAllVehiclesByOwner(Integer ownerId);
    Vehicle       getVehicleById(Integer id);
    Vehicle       getVehicleByRegistrationNumber(String registrationNumber);
    List<Vehicle> getVehiclesByType(Integer ownerId, String type);
    List<Vehicle> getVehiclesByFuelType(Integer ownerId, String fuelType);
    List<Vehicle> searchVehicles(String keyword, Integer ownerId);
    Vehicle       addVehicle(VehicleRequestDto dto);
    Vehicle       updateVehicle(Integer id, VehicleRequestDto dto);
    void          deleteVehicle(Integer id);

    List<Vehicle> getVehiclesByOwner(Integer ownerId);
}