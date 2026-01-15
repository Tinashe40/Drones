package com.tinashe.dronesbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicationDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;

    @Pattern(regexp = "^[a-zA-Z0-9_-]+$")
    @NotNull
    private String name;

    @DecimalMin(value = "0.0")
    private int weight;

    @Pattern(regexp = "^[A-Z0-9_]+$")
    @NotNull
    private String code;

    private String imageUrl;
}