package com.tinashe.dronesbackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Data
public class Medication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Pattern(regexp = "^[a-zA-Z0-9_-]+$")
    @NotNull
    private String name;

    @DecimalMin(value = "0.0")
    private double weight;

    @Pattern(regexp = "^[A-Z0-9_]+$")
    @NotNull
    private String code;

    private String imageUrl;
}
