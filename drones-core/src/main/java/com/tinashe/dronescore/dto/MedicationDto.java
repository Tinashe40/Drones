package com.tinashe.dronescore.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tinashe.dronescore.enums.MedicationStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel; // Import for RepresentationModel

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicationDto extends RepresentationModel<MedicationDto> { // Extend RepresentationModel

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

    private int quantity;

    private MedicationStatus medicationStatus;
}