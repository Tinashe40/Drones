package com.tinashe.dronesbackend.controller;

import com.tinashe.dronesbackend.dto.DroneDto;
import com.tinashe.dronesbackend.dto.LoadMedicationRequest;
import com.tinashe.dronesbackend.dto.MedicationDto;
import com.tinashe.dronesbackend.service.DroneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drones")
@RequiredArgsConstructor
@Tag(name = "Drone Controller", description = "Endpoints for managing drones")
public class DroneController {

    private final DroneService droneService;

    @PostMapping
    @Operation(summary = "Register a new drone")
    @ApiResponse(responseCode = "201", description = "Drone registered successfully")
    public ResponseEntity<DroneDto> registerDrone(@Valid @RequestBody DroneDto droneDto) {
        DroneDto registeredDrone = droneService.registerDrone(droneDto);
        return new ResponseEntity<>(registeredDrone, HttpStatus.CREATED);
    }

    @PostMapping("/{serialNumber}/load")
    @Operation(summary = "Load a drone with medication items")
    @ApiResponse(responseCode = "200", description = "Drone loaded successfully")
    public ResponseEntity<Void> loadMedication(@PathVariable String serialNumber,
                                                 @Valid @RequestBody LoadMedicationRequest request) {
        droneService.loadMedication(serialNumber, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{serialNumber}/medications")
    @Operation(summary = "Get loaded medication items for a drone")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved medication items")
    public ResponseEntity<List<MedicationDto>> getLoadedMedication(@PathVariable String serialNumber) {
        List<MedicationDto> medications = droneService.getLoadedMedication(serialNumber);
        return ResponseEntity.ok(medications);
    }

    @GetMapping("/available")
    @Operation(summary = "Get available drones for loading")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved available drones")
    public ResponseEntity<List<DroneDto>> getAvailableDrones() {
        List<DroneDto> availableDrones = droneService.getAvailableDrones();
        return ResponseEntity.ok(availableDrones);
    }

    @GetMapping("/{serialNumber}/battery")
    @Operation(summary = "Get drone battery level")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved battery level")
    public ResponseEntity<Integer> getDroneBatteryLevel(@PathVariable String serialNumber) {
        int batteryLevel = droneService.getDroneBatteryLevel(serialNumber);
        return ResponseEntity.ok(batteryLevel);
    }
}