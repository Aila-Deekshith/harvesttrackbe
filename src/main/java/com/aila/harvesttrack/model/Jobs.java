package com.aila.harvesttrack.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Jobs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonBackReference("customer-jobs")
    @ManyToOne
    @JoinColumn(name = "customerId", nullable = false)
    private Customer customer;

    @JsonBackReference("vehicle-jobs")
    @ManyToOne
    @JoinColumn(name = "vehicleId", nullable = false)
    private Vehicle vehicle;

    private String  description;
    private String  status;
    private Instant startDate;
    private Instant endDate;

    @JoinColumn(name = "activityId")
    @ManyToOne
    @JsonBackReference("activity-jobs")
    private Activity activity;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    private Instant deletedAt;

    private Float acres;
    private Float cost;

    @JsonBackReference("owner-jobs")
    @ManyToOne
    @JoinColumn(name = "ownerId")
    private Owner owner;

    private String paymentStatus;
}