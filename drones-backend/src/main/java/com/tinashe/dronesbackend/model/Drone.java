package com.tinashe.dronesbackend.model;

import com.tinashe.dronesbackend.common.jpa.BaseEntity;
import com.tinashe.dronesbackend.enums.DroneModel;
import com.tinashe.dronesbackend.enums.DroneState;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@GenericGenerator(name = "custom-id", strategy = "com.tinashe.dronesbackend.common.jpa.id.CustomIdGenerator", parameters = @org.hibernate.annotations.Parameter(name = "prefix", value = "drn"))
public class Drone extends BaseEntity {

    @Column(length = 100, unique = true, nullable = false)
    private String serialNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DroneModel model;

    @Column(nullable = false)
    private int weightLimit;

    @Column(nullable = false)
    private int batteryCapacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DroneState state;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "drone_id")
    private List<Medication> medications = new ArrayList<>();

    @Override
    public String toString() {
        return "Drone{"
                + "serialNumber='" + serialNumber + "'"
                + ", model=" + model
                + ", weightLimit=" + weightLimit
                + ", batteryCapacity=" + batteryCapacity
                + ", state=" + state
                + "}";
    }
}
