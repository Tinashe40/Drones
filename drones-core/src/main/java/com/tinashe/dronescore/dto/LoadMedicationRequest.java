package com.tinashe.dronescore.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class LoadMedicationRequest {

    @NotEmpty(message = "Medication codes cannot be empty")
    private List<String> medicationCodes;
}