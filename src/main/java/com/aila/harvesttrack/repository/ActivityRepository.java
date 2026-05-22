package com.aila.harvesttrack.repository;

import com.aila.harvesttrack.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Integer> {

    // ── FIND METHODS ──────────────────────────────

    // Find activity by ID
    Optional<Activity> findByIdAndDeletedAtIsNull(Integer id);

    // Find by crop type
    List<Activity> findByCropTypeAndDeletedAtIsNull(String cropType);



    // ── UPDATE METHODS ────────────────────────────

    // Update activity name
    @Modifying
    @Transactional
    @Query("""
        UPDATE Activity a
        SET a.activityName = :activityName
        WHERE a.id = :id
        AND a.deletedAt IS NULL
    """)
    int updateActivityNameById(
            @Param("id")           Integer id,
            @Param("activityName") String  activityName
    );

    // Update crop type
    @Modifying
    @Transactional
    @Query("""
        UPDATE Activity a
        SET a.cropType = :cropType
        WHERE a.id = :id
        AND a.deletedAt IS NULL
    """)
    int updateCropTypeById(
            @Param("id")       Integer id,
            @Param("cropType") String  cropType
    );

    // Soft delete
    @Modifying
    @Transactional
    @Query("UPDATE Activity a SET a.deletedAt = CURRENT_TIMESTAMP WHERE a.id = :id")
    int softDeleteById(@Param("id") Integer id);
}