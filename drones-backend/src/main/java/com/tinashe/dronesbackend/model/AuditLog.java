package com.tinashe.dronesbackend.model;

import com.tinashe.dronesbackend.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AuditLog extends BaseEntity {

    @NotNull
    private String droneSerialNumber;

    @NotNull
    private int batteryCapacity;

    private String eventType;

    public AuditLog(String droneSerialNumber, int batteryCapacity, String eventType) {
        this.droneSerialNumber = droneSerialNumber;
        this.batteryCapacity = batteryCapacity;
        this.eventType = eventType;
    }
}
