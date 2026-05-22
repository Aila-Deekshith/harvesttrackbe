package com.aila.harvesttrack.repository;

import com.aila.harvesttrack.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

    // ── FIND METHODS ──────────────────────────────

    // Find all active vehicles by owner
    List<Vehicle> findByOwnerIdAndDeletedAtIsNull(Integer ownerId);

    // Find vehicle by ID and owner
    Optional<Vehicle> findByIdAndOwnerIdAndDeletedAtIsNull(
            Integer id, Integer ownerId
    );

    // Find by registration number
    Optional<Vehicle> findByRegistrationNumberAndDeletedAtIsNull(
            String registrationNumber
    );

    // Check duplicate registration number
    boolean existsByRegistrationNumberAndDeletedAtIsNull(
            String registrationNumber
    );

    // Check duplicate registration number excluding current vehicle
    boolean existsByRegistrationNumberAndIdNotAndDeletedAtIsNull(
            String registrationNumber, Integer id
    );

    // Find by fuel type and owner
    List<Vehicle> findByFuelTypeAndOwnerIdAndDeletedAtIsNull(
            String fuelType, Integer ownerId
    );

    // Find by type and owner
    List<Vehicle> findByTypeAndOwnerIdAndDeletedAtIsNull(
            String type, Integer ownerId
    );

    // Search by name or registration number
    List<Vehicle> findByOwnerIdAndDeletedAtIsNullAndNameContainingIgnoreCaseOrOwnerIdAndDeletedAtIsNullAndRegistrationNumberContainingIgnoreCase(
            Integer ownerId1, String name,
            Integer ownerId2, String registrationNumber
    );

    // ── UPDATE METHODS ────────────────────────────

    // Update name
    @Modifying
    @Transactional
    @Query("UPDATE Vehicle v SET v.name = :name WHERE v.id = :id AND v.deletedAt IS NULL")
    int updateNameById(
            @Param("id")   Integer id,
            @Param("name") String  name
    );

    // Update fuel type
    @Modifying
    @Transactional
    @Query("UPDATE Vehicle v SET v.fuelType = :fuelType WHERE v.id = :id AND v.deletedAt IS NULL")
    int updateFuelTypeById(
            @Param("id")       Integer id,
            @Param("fuelType") String  fuelType
    );

    // Update registration number
    @Modifying
    @Transactional
    @Query("""
        UPDATE Vehicle v
        SET v.registrationNumber = :regNumber
        WHERE v.id = :id
        AND v.deletedAt IS NULL
    """)
    int updateRegistrationNumberById(
            @Param("id")        Integer id,
            @Param("regNumber") String  regNumber
    );

    // Update all fields
    @Modifying
    @Transactional
    @Query("""
        UPDATE Vehicle v
        SET v.name               = :name,
            v.type               = :type,
            v.registrationNumber = :regNumber,
            v.fuelType           = :fuelType
        WHERE v.id = :id
        AND v.deletedAt IS NULL
    """)
    int updateVehicleById(
            @Param("id")        Integer id,
            @Param("name")      String  name,
            @Param("type")      String  type,
            @Param("regNumber") String  regNumber,
            @Param("fuelType")  String  fuelType
    );

    // Soft delete
    @Modifying
    @Transactional
    @Query("UPDATE Vehicle v SET v.deletedAt = CURRENT_TIMESTAMP WHERE v.id = :id")
    int softDeleteById(@Param("id") Integer id);

    Optional<Object> findByIdAndDeletedAtIsNull(Integer id);
}