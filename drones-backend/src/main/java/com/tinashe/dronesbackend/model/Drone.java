package com.tinashe.dronesbackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Drone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @Size(max = 100)
    @NotNull
    private String serialNumber;

    @Enumerated(EnumType.STRING)
    @NotNull
    private DroneModel model;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "500.0")
    private double weightLimit; // grams

    @Min(0)
    @Max(100)
    private int batteryCapacity; // percentage

    @Enumerated(EnumType.STRING)
    @NotNull
    private DroneState state;

    @ManyToMany
    @JoinTable(
            name = "drone_medications",
            joinColumns = @JoinColumn(name = "drone_id"),
            inverseJoinColumns = @JoinColumn(name = "medication_id")
    )
    private List<Medication> medications;
}
