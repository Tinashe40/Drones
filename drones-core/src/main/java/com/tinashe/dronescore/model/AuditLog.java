package com.tinashe.dronescore.model;

import com.tinashe.dronescore.common.jpa.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime; // Import for LocalDateTime

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@GenericGenerator(name = "custom-id", strategy = "com.tinashe.dronescore.common.jpa.id.CustomIdGenerator", parameters = @org.hibernate.annotations.Parameter(name = "prefix", value = "aud"))
public class AuditLog extends BaseEntity {

    @NotNull
    private String droneSerialNumber;

    @NotNull
    private int batteryCapacity;

    private String eventType;
    private String userId; // New field for the user who triggered the event
    private LocalDateTime timestamp; // New field for timestamp
    private String details; // New field for additional details (e.g., JSON string)

    public AuditLog(String droneSerialNumber, int batteryCapacity, String eventType) {
        this.droneSerialNumber = droneSerialNumber;
        this.batteryCapacity = batteryCapacity;
        this.eventType = eventType;
        this.timestamp = LocalDateTime.now(); // Default to current time
    }

    public AuditLog(String droneSerialNumber, int batteryCapacity, String eventType, String userId, String details) {
        this.droneSerialNumber = droneSerialNumber;
        this.batteryCapacity = batteryCapacity;
        this.eventType = eventType;
        this.userId = userId;
        this.timestamp = LocalDateTime.now(); // Default to current time
        this.details = details;
    }
}
