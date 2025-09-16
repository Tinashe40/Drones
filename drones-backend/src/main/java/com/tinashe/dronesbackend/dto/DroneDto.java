package com.tinashe.dronesbackend.dto;

import com.tinashe.dronesbackend.enums.DroneModel;
import com.tinashe.dronesbackend.enums.DroneState;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DroneDto {

    @NotBlank
    @Size(max = 100)
    private String serialNumber;

    @NotNull
    private DroneModel model;

    @Max(500)
    @PositiveOrZero
    private int weightLimit;

    @Min(0)
    @Max(100)
    @PositiveOrZero
    private int batteryCapacity;

    @NotNull
    private DroneState state;
}