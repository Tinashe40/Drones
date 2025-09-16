package com.tinashe.dronesbackend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class MedicationDto {

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
