package com.tinashe.dronesbackend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class LoadMedicationRequest {

    @NotNull
    @Size(min = 1, message = "At least one medication item must be provided")
    @Valid
    private List<MedicationDto> medications;
}
