package com.tinashe.dronesbackend.model;

import com.tinashe.dronesbackend.common.jpa.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@GenericGenerator(name = "custom-id", strategy = "com.tinashe.dronesbackend.common.jpa.id.CustomIdGenerator", parameters = @org.hibernate.annotations.Parameter(name = "prefix", value = "aud"))
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
