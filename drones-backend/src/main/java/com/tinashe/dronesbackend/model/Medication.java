package com.tinashe.dronesbackend.model;

import com.tinashe.dronesbackend.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
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