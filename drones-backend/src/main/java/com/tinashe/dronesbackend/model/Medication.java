package com.tinashe.dronesbackend.model;

import com.tinashe.dronesbackend.common.jpa.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@GenericGenerator(name = "custom-id", strategy = "com.tinashe.dronesbackend.common.jpa.id.CustomIdGenerator", parameters = @org.hibernate.annotations.Parameter(name = "prefix", value = "med"))
public class Medication extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int weight;

    @Column(nullable = false, unique = true)
    private String code;

    private String imageUrl;

    @Override
    public String toString() {
        return "Medication{"
                + "name='" + name + "'" +
                ", weight=" + weight +
                ", code='" + code + "'" +
                ", imageUrl='" + imageUrl + "'" +
                "}";
    }
}