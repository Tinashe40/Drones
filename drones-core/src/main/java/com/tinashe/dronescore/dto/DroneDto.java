package com.tinashe.dronescore.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tinashe.dronescore.enums.DroneModel;
import com.tinashe.dronescore.enums.DroneState;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel; // Import for RepresentationModel

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DroneDto extends RepresentationModel<DroneDto> { // Extend RepresentationModel

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;

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

    // Predicted metrics (read-only, optional)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer predictedFlightTimeMinutes;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer predictedRangeKm;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer predictedChargingTimeMinutes;
}