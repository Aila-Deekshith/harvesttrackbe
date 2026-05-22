package com.aila.harvesttrack.repository;

import com.aila.harvesttrack.model.Jobs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobsRepository extends JpaRepository<Jobs, Integer> {

    // ── Find all active jobs (not soft deleted)
    List<Jobs> findByDeletedAtIsNull();

    // ── Find job by ID (not soft deleted)
    Optional<Jobs> findByIdAndDeletedAtIsNull(Integer id);

    // ── Find all jobs by customer
    List<Jobs> findByCustomerIdAndDeletedAtIsNull(Integer customerId);

    // ── Find all jobs by vehicle
    List<Jobs> findByVehicleIdAndDeletedAtIsNull(Integer vehicleId);

    // ── Find jobs by status
    List<Jobs> findByStatusAndDeletedAtIsNull(String status);

    // ── Find jobs by customer and status
    List<Jobs> findByCustomerIdAndStatusAndDeletedAtIsNull(
            Integer customerId, String status
    );

    // ── Find jobs between date range
    List<Jobs> findByStartDateBetweenAndDeletedAtIsNull(
            Instant startDate, Instant endDate
    );

    // ── Find jobs by owner (through customer)
    @Query("""
        SELECT j FROM Jobs j
        WHERE j.customer.owner.id = :ownerId
        AND j.deletedAt IS NULL
        ORDER BY j.createdAt DESC
    """)
    List<Jobs> findByOwnerIdAndDeletedAtIsNull(
            @Param("ownerId") Integer ownerId
    );

    // ── Find jobs by owner and status
    @Query("""
        SELECT j FROM Jobs j
        WHERE j.customer.owner.id = :ownerId
        AND j.status = :status
        AND j.deletedAt IS NULL
    """)
    List<Jobs> findByOwnerIdAndStatus(
            @Param("ownerId") Integer ownerId,
            @Param("status")  String  status
    );

    // ── Count jobs by owner
    @Query("""
        SELECT COUNT(j) FROM Jobs j
        WHERE j.customer.owner.id = :ownerId
        AND j.deletedAt IS NULL
    """)
    long countByOwnerId(@Param("ownerId") Integer ownerId);
}