package com.tinashe.dronesbackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String droneSerialNumber;

    @NotNull
    private int batteryCapacity;

    @NotNull
    private LocalDateTime timestamp;

    private String eventType;
}
