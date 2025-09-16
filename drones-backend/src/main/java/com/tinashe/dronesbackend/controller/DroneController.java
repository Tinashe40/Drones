package com.tinashe.dronesbackend.controller;

import com.tinashe.dronesbackend.dto.DroneDto;
import com.tinashe.dronesbackend.dto.LoadMedicationRequest;
import com.tinashe.dronesbackend.dto.MedicationDto;
import com.tinashe.dronesbackend.model.Drone;
import com.tinashe.dronesbackend.service.DroneService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drones")
@AllArgsConstructor
public class DroneController {

    private final DroneService droneService;

    @PostMapping
    public ResponseEntity<Drone> registerDrone(@Valid @RequestBody DroneDto droneDto) {
        Drone registeredDrone = droneService.registerDrone(droneDto);
        return new ResponseEntity<>(registeredDrone, HttpStatus.CREATED);
    }

    @PostMapping("/{serialNumber}/load")
    public ResponseEntity<Drone> loadMedications(@PathVariable String serialNumber, @Valid @RequestBody LoadMedicationRequest request) {
        Drone updatedDrone = droneService.loadMedications(serialNumber, request.getMedications());
        return new ResponseEntity<>(updatedDrone, HttpStatus.OK);
    }

    @GetMapping("/{serialNumber}/medications")
    public ResponseEntity<List<MedicationDto>> getLoadedMedications(@PathVariable String serialNumber) {
        List<MedicationDto> medications = droneService.getLoadedMedications(serialNumber);
        return new ResponseEntity<>(medications, HttpStatus.OK);
    }

    @GetMapping("/available")
    public ResponseEntity<List<DroneDto>> getAvailableDronesForLoading() {
        List<DroneDto> availableDrones = droneService.getAvailableDronesForLoading();
        return new ResponseEntity<>(availableDrones, HttpStatus.OK);
    }

    @GetMapping("/{serialNumber}/battery")
    public ResponseEntity<Integer> getDroneBatteryLevel(@PathVariable String serialNumber) {
        int batteryLevel = droneService.getDroneBatteryLevel(serialNumber);
        return new ResponseEntity<>(batteryLevel, HttpStatus.OK);
    }
}
