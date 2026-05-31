package com.aila.harvesttrack.repository;

import com.aila.harvesttrack.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Integer> {

    // ── Find by phone (for login)
    Optional<Owner> findByPhone(String phone);

    // ── Find active owner by phone
    Optional<Owner> findByPhoneAndDeletedAtIsNull(String phone);

    // ── Find active owner by ID
    Optional<Owner> findByIdAndDeletedAtIsNull(Integer id);

    // ── Check if phone exists
    boolean existsByPhone(String phone);

    // ── Check if phone exists excluding current owner
    boolean existsByPhoneAndIdNot(String phone, Integer id);

    // ── Get all active owners
    List<Owner> findByDeletedAtIsNull();

    // ── Search by name or phone
    List<Owner> findByDeletedAtIsNullAndNameContainingIgnoreCaseOrDeletedAtIsNullAndPhoneContaining(
            String name, String phone
    );

    // ── Update name and address
    @Modifying
    @Transactional
    @Query("""
        UPDATE Owner o
        SET o.name    = :name,
            o.address = :address
        WHERE o.id = :id
        AND o.deletedAt IS NULL
    """)
    int updateProfileById(
            @Param("id")      Integer id,
            @Param("name")    String  name,
            @Param("address") String  address
    );

    // ── Update password
    @Modifying
    @Transactional
    @Query("""
        UPDATE Owner o
        SET o.password = :password
        WHERE o.id = :id
        AND o.deletedAt IS NULL
    """)
    int updatePasswordById(
            @Param("id")       Integer id,
            @Param("password") String  password
    );

    // ── Soft delete
    @Modifying
    @Transactional
    @Query("UPDATE Owner o SET o.deletedAt = CURRENT_TIMESTAMP WHERE o.id = :id")
    int softDeleteById(@Param("id") Integer id);

    List<Owner> findByNameContainingIgnoreCaseOrPhoneContaining(String keyword, String keyword1);
}