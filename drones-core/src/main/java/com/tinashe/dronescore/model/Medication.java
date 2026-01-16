package com.tinashe.dronescore.model;

import com.tinashe.dronescore.common.jpa.BaseEntity;
import com.tinashe.dronescore.enums.MedicationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Version; // Import for Version
import lombok.*;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@GenericGenerator(name = "custom-id", strategy = "com.tinashe.dronescore.common.jpa.id.CustomIdGenerator", parameters = @org.hibernate.annotations.Parameter(name = "prefix", value = "med"))
public class Medication extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int weight;

    @Column(nullable = false, unique = true)
    private String code;

    private String imageUrl;

    private int quantity; // New field for inventory tracking

    @Enumerated(EnumType.STRING) // New field for medication lifecycle status
    private MedicationStatus medicationStatus;

    @Version // Optimistic locking field
    private int version;

    @Override
    public String toString() {
        return "Medication{"
                + "name='" + name + "'" +
                ", weight=" + weight +
                ", code='" + code + "'" +
                ", imageUrl='" + imageUrl + "'" +
                ", quantity=" + quantity +
                ", medicationStatus=" + medicationStatus +
                "}";
    }
}