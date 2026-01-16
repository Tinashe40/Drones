package com.tinashe.dronescore.model;

import com.tinashe.dronescore.common.jpa.BaseEntity;
import com.tinashe.dronescore.enums.DroneModel;
import com.tinashe.dronescore.enums.DroneState;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@GenericGenerator(name = "custom-id", strategy = "com.tinashe.dronescore.common.jpa.id.CustomIdGenerator", parameters = @org.hibernate.annotations.Parameter(name = "prefix", value = "drn"))
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

    @Version // Optimistic locking field
    private int version;

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
