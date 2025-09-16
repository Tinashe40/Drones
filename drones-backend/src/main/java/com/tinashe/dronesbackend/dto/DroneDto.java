package com.tinashe.dronesbackend.dto;

import com.tinashe.dronesbackend.model.DroneModel;
import com.tinashe.dronesbackend.model.DroneState;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DroneDto {

    @Size(max = 100)
    @NotNull
    private String serialNumber;

    @NotNull
    private DroneModel model;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "500.0")
    private double weightLimit;

    @Min(0)
    @Max(100)
    private int batteryCapacity;

    @NotNull
    private DroneState state;
}
