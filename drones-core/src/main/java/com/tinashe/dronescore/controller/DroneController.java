package com.tinashe.dronescore.controller;

import com.tinashe.dronescore.dto.DroneDto;
import com.tinashe.dronescore.dto.LoadMedicationRequest;
import com.tinashe.dronescore.dto.MedicationDto;
import com.tinashe.dronescore.enums.DroneState;
import com.tinashe.dronescore.enums.Role;
import com.tinashe.dronescore.dto.AuditLogDto;
import com.tinashe.dronescore.dto.PageResponseDto;
import com.tinashe.dronescore.service.DroneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder; // Import WebMvcLinkBuilder
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*; // Static import for linkTo, methodOn
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/drones")
@RequiredArgsConstructor
@Tag(name = "Drone Controller", description = "Endpoints for managing drones")
public class DroneController {

    private final DroneService droneService;

    @PostMapping
    @Operation(summary = "Register a new drone")
    @ApiResponse(responseCode = "201", description = "Drone registered successfully")
    @PreAuthorize("hasAuthority('" + Role.ADMIN + "')")
    public ResponseEntity<DroneDto> registerDrone(@Valid @RequestBody DroneDto droneDto) {
        DroneDto registeredDrone = droneService.registerDrone(droneDto);
        registeredDrone.add(linkTo(methodOn(DroneController.class).getDroneBySerialNumber(registeredDrone.getSerialNumber())).withSelfRel());
        return new ResponseEntity<>(registeredDrone, HttpStatus.CREATED);
    }

    @PostMapping("/{serialNumber}/load")
    @Operation(summary = "Load a drone with medication items")
    @ApiResponse(responseCode = "200", description = "Drone loaded successfully")
    @PreAuthorize("hasAnyAuthority('" + Role.ADMIN + "', '" + Role.DISPATCHER + "')")
    public ResponseEntity<Void> loadMedication(@PathVariable String serialNumber,
                                                 @Valid @RequestBody LoadMedicationRequest request) {
        droneService.loadMedication(serialNumber, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{serialNumber}/initiate-loading")
    @Operation(summary = "Initiate loading process for a drone")
    @ApiResponse(responseCode = "200", description = "Loading initiated successfully")
    @PreAuthorize("hasAnyAuthority('" + Role.ADMIN + "', '" + Role.DISPATCHER + "')")
    public ResponseEntity<DroneDto> initiateLoading(@PathVariable String serialNumber) {
        DroneDto droneDto = droneService.initiateLoading(serialNumber);
        droneDto.add(linkTo(methodOn(DroneController.class).initiateLoading(serialNumber)).withSelfRel());
        droneDto.add(linkTo(methodOn(DroneController.class).completeLoading(serialNumber)).withRel("complete-loading"));
        return ResponseEntity.ok(droneDto);
    }

    @PostMapping("/{serialNumber}/complete-loading")
    @Operation(summary = "Complete loading process for a drone")
    @ApiResponse(responseCode = "200", description = "Loading completed successfully")
    @PreAuthorize("hasAnyAuthority('" + Role.ADMIN + "', '" + Role.DISPATCHER + "')")
    public ResponseEntity<DroneDto> completeLoading(@PathVariable String serialNumber) {
        DroneDto droneDto = droneService.completeLoading(serialNumber);
        droneDto.add(linkTo(methodOn(DroneController.class).completeLoading(serialNumber)).withSelfRel());
        droneDto.add(linkTo(methodOn(DroneController.class).dispatchDrone(serialNumber)).withRel("dispatch"));
        return ResponseEntity.ok(droneDto);
    }

    @PostMapping("/{serialNumber}/dispatch")
    @Operation(summary = "Dispatch a loaded drone for delivery")
    @ApiResponse(responseCode = "200", description = "Drone dispatched successfully")
    @PreAuthorize("hasAnyAuthority('" + Role.ADMIN + "', '" + Role.DISPATCHER + "')")
    public ResponseEntity<DroneDto> dispatchDrone(@PathVariable String serialNumber) {
        DroneDto droneDto = droneService.dispatchDrone(serialNumber);
        droneDto.add(linkTo(methodOn(DroneController.class).dispatchDrone(serialNumber)).withSelfRel());
        droneDto.add(linkTo(methodOn(DroneController.class).deliverMedication(serialNumber)).withRel("deliver"));
        return ResponseEntity.ok(droneDto);
    }

    @PostMapping("/{serialNumber}/deliver")
    @Operation(summary = "Mark a drone's delivery as completed")
    @ApiResponse(responseCode = "200", description = "Delivery marked as complete")
    @PreAuthorize("hasAnyAuthority('" + Role.ADMIN + "', '" + Role.DISPATCHER + "')")
    public ResponseEntity<DroneDto> deliverMedication(@PathVariable String serialNumber) {
        DroneDto droneDto = droneService.deliverMedication(serialNumber);
        droneDto.add(linkTo(methodOn(DroneController.class).deliverMedication(serialNumber)).withSelfRel());
        droneDto.add(linkTo(methodOn(DroneController.class).returnDrone(serialNumber)).withRel("return"));
        droneDto.add(linkTo(methodOn(DroneController.class).unloadDrone(serialNumber)).withRel("unload")); // Option to unload if at base
        return ResponseEntity.ok(droneDto);
    }

    @PostMapping("/{serialNumber}/return")
    @Operation(summary = "Mark a drone as returning to base")
    @ApiResponse(responseCode = "200", description = "Drone marked as returning")
    @PreAuthorize("hasAnyAuthority('" + Role.ADMIN + "', '" + Role.DISPATCHER + "')")
    public ResponseEntity<DroneDto> returnDrone(@PathVariable String serialNumber) {
        DroneDto droneDto = droneService.returnDrone(serialNumber);
        droneDto.add(linkTo(methodOn(DroneController.class).returnDrone(serialNumber)).withSelfRel());
        droneDto.add(linkTo(methodOn(DroneController.class).startCharging(serialNumber)).withRel("start-charging")); // Once returned, can start charging
        droneDto.add(linkTo(methodOn(DroneController.class).unloadDrone(serialNumber)).withRel("unload")); // If returned with meds
        return ResponseEntity.ok(droneDto);
    }

    @PostMapping("/{serialNumber}/start-charging")
    @Operation(summary = "Start charging a drone")
    @ApiResponse(responseCode = "200", description = "Charging started successfully")
    @PreAuthorize("hasAnyAuthority('" + Role.ADMIN + "', '" + Role.MAINTENANCE + "')")
    public ResponseEntity<DroneDto> startCharging(@PathVariable String serialNumber) {
        DroneDto droneDto = droneService.startCharging(serialNumber);
        droneDto.add(linkTo(methodOn(DroneController.class).startCharging(serialNumber)).withSelfRel());
        droneDto.add(linkTo(methodOn(DroneController.class).completeCharging(serialNumber)).withRel("complete-charging"));
        return ResponseEntity.ok(droneDto);
    }

    @PostMapping("/{serialNumber}/complete-charging")
    @Operation(summary = "Complete charging for a drone")
    @ApiResponse(responseCode = "200", description = "Charging completed successfully")
    @PreAuthorize("hasAnyAuthority('" + Role.ADMIN + "', '" + Role.MAINTENANCE + "')")
    public ResponseEntity<DroneDto> completeCharging(@PathVariable String serialNumber) {
        DroneDto droneDto = droneService.completeCharging(serialNumber);
        droneDto.add(linkTo(methodOn(DroneController.class).completeCharging(serialNumber)).withSelfRel());
        // Could add links for other actions from IDLE state
        return ResponseEntity.ok(droneDto);
    }

    @PostMapping("/{serialNumber}/unload")
    @Operation(summary = "Unload medications from a drone")
    @ApiResponse(responseCode = "200", description = "Medications unloaded successfully")
    @PreAuthorize("hasAnyAuthority('" + Role.ADMIN + "', '" + Role.DISPATCHER + "')")
    public ResponseEntity<DroneDto> unloadDrone(@PathVariable String serialNumber) {
        DroneDto droneDto = droneService.unloadDrone(serialNumber);
        droneDto.add(linkTo(methodOn(DroneController.class).unloadDrone(serialNumber)).withSelfRel());
        // Could add links for other actions from IDLE state
        return ResponseEntity.ok(droneDto);
    }

    @GetMapping("/{serialNumber}/medications")
    @Operation(summary = "Get loaded medication items for a drone")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved medication items")
    @PreAuthorize("hasAnyAuthority('" + Role.ADMIN + "', '" + Role.DISPATCHER + "', '" + Role.USER + "')")
    public ResponseEntity<PageResponseDto<MedicationDto>> getLoadedMedication(
            @PathVariable String serialNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponseDto<MedicationDto> medications = droneService.getLoadedMedication(serialNumber, page, size);
        medications.getContent().forEach(medicationDto ->
            medicationDto.add(linkTo(methodOn(MedicationController.class).getMedicationByCode(medicationDto.getCode())).withSelfRel()));
        medications.add(linkTo(methodOn(DroneController.class).getLoadedMedication(serialNumber, page, size)).withSelfRel());
        return ResponseEntity.ok(medications);
    }

    @GetMapping("/available")
    @Operation(summary = "Get available drones for loading")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved available drones")
    @PreAuthorize("hasAnyAuthority('" + Role.ADMIN + "', '" + Role.DISPATCHER + "', '" + Role.USER + "')")
    public ResponseEntity<PageResponseDto<DroneDto>> getAvailableDrones(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponseDto<DroneDto> availableDrones = droneService.getAvailableDrones(page, size);
        availableDrones.getContent().forEach(droneDto ->
            droneDto.add(linkTo(methodOn(DroneController.class).getDroneBySerialNumber(droneDto.getSerialNumber())).withSelfRel()));
        availableDrones.add(linkTo(methodOn(DroneController.class).getAvailableDrones(page, size)).withSelfRel());
        return ResponseEntity.ok(availableDrones);
    }

    @GetMapping
    @Operation(summary = "Get all drones, optionally filtered by state")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved drones")
    @PreAuthorize("hasAnyAuthority('" + Role.ADMIN + "', '" + Role.DISPATCHER + "', '" + Role.USER + "', '" + Role.MAINTENANCE + "')")
    public ResponseEntity<PageResponseDto<DroneDto>> getAllDrones(
            @RequestParam(required = false) DroneState state,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponseDto<DroneDto> drones = droneService.getAllDrones(state, page, size);
        drones.getContent().forEach(droneDto ->
            droneDto.add(linkTo(methodOn(DroneController.class).getDroneBySerialNumber(droneDto.getSerialNumber())).withSelfRel()));
        drones.add(linkTo(methodOn(DroneController.class).getAllDrones(state, page, size)).withSelfRel());
        return ResponseEntity.ok(drones);
    }

    @GetMapping("/{serialNumber}/battery")
    @Operation(summary = "Get drone battery level")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved battery level")
    @PreAuthorize("hasAnyAuthority('" + Role.ADMIN + "', '" + Role.DISPATCHER + "', '" + Role.USER + "', '" + Role.MAINTENANCE + "')")
    public ResponseEntity<Integer> getDroneBatteryLevel(@PathVariable String serialNumber) {
        int batteryLevel = droneService.getDroneBatteryLevel(serialNumber);
        // This is a primitive, so can't directly add links to it.
        return ResponseEntity.ok(batteryLevel);
    }

    @GetMapping("/{serialNumber}")
    @Operation(summary = "Get a drone by serial number")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved drone details")
    @ApiResponse(responseCode = "404", description = "Drone not found")
    @PreAuthorize("hasAnyAuthority('" + Role.ADMIN + "', '" + Role.DISPATCHER + "', '" + Role.USER + "', '" + Role.MAINTENANCE + "')")
    public ResponseEntity<DroneDto> getDroneBySerialNumber(@PathVariable String serialNumber) {
        DroneDto droneDto = droneService.getDroneBySerialNumber(serialNumber);
        droneDto.add(linkTo(methodOn(DroneController.class).getDroneBySerialNumber(serialNumber)).withSelfRel());
        droneDto.add(linkTo(methodOn(DroneController.class).updateDrone(serialNumber, new DroneDto())).withRel("update-drone"));
        droneDto.add(linkTo(methodOn(DroneController.class).deleteDrone(serialNumber)).withRel("delete-drone"));
        droneDto.add(linkTo(methodOn(DroneController.class).getDroneAuditLogs(serialNumber, 0, 10)).withRel("audit-logs"));
        droneDto.add(linkTo(methodOn(DroneController.class).getLoadedMedication(serialNumber, 0, 10)).withRel("loaded-medications"));
        droneDto.add(linkTo(methodOn(DroneController.class).getDronePredictions(serialNumber)).withRel("predictions"));
        // Add more state transition links based on current state
        if (droneDto.getState() == DroneState.IDLE) {
            droneDto.add(linkTo(methodOn(DroneController.class).initiateLoading(serialNumber)).withRel("initiate-loading"));
            droneDto.add(linkTo(methodOn(DroneController.class).startCharging(serialNumber)).withRel("start-charging"));
        } else if (droneDto.getState() == DroneState.LOADING) {
            droneDto.add(linkTo(methodOn(DroneController.class).completeLoading(serialNumber)).withRel("complete-loading"));
        } else if (droneDto.getState() == DroneState.LOADED) {
            droneDto.add(linkTo(methodOn(DroneController.class).dispatchDrone(serialNumber)).withRel("dispatch"));
            droneDto.add(linkTo(methodOn(DroneController.class).unloadDrone(serialNumber)).withRel("unload"));
        } else if (droneDto.getState() == DroneState.DELIVERING) {
            droneDto.add(linkTo(methodOn(DroneController.class).deliverMedication(serialNumber)).withRel("deliver"));
        } else if (droneDto.getState() == DroneState.DELIVERED) {
            droneDto.add(linkTo(methodOn(DroneController.class).returnDrone(serialNumber)).withRel("return"));
            droneDto.add(linkTo(methodOn(DroneController.class).unloadDrone(serialNumber)).withRel("unload"));
        } else if (droneDto.getState() == DroneState.RETURNING) {
            droneDto.add(linkTo(methodOn(DroneController.class).startCharging(serialNumber)).withRel("start-charging"));
            droneDto.add(linkTo(methodOn(DroneController.class).unloadDrone(serialNumber)).withRel("unload"));
        } else if (droneDto.getState() == DroneState.CHARGING) {
            droneDto.add(linkTo(methodOn(DroneController.class).completeCharging(serialNumber)).withRel("complete-charging"));
        }
        return ResponseEntity.ok(droneDto);
    }

    @GetMapping("/{serialNumber}/predictions")
    @Operation(summary = "Get drone predictions (flight time, range, charging time)")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved drone predictions")
    @ApiResponse(responseCode = "404", description = "Drone not found")
    @PreAuthorize("hasAnyAuthority('" + Role.ADMIN + "', '" + Role.DISPATCHER + "', '" + Role.USER + "', '" + Role.MAINTENANCE + "')")
    public ResponseEntity<DroneDto> getDronePredictions(@PathVariable String serialNumber) {
        DroneDto droneDto = droneService.getDronePredictions(serialNumber);
        droneDto.add(linkTo(methodOn(DroneController.class).getDronePredictions(serialNumber)).withSelfRel());
        droneDto.add(linkTo(methodOn(DroneController.class).getDroneBySerialNumber(serialNumber)).withRel("drone-details"));
        return ResponseEntity.ok(droneDto);
    }

    @PutMapping("/{serialNumber}")
    @Operation(summary = "Update a drone by serial number")
    @ApiResponse(responseCode = "200", description = "Successfully updated drone details")
    @ApiResponse(responseCode = "404", description = "Drone not found")
    @PreAuthorize("hasAnyAuthority('" + Role.ADMIN + "', '" + Role.MAINTENANCE + "')")
    public ResponseEntity<DroneDto> updateDrone(@PathVariable String serialNumber, @Valid @RequestBody DroneDto droneDto) {
        DroneDto updatedDrone = droneService.updateDrone(serialNumber, droneDto);
        updatedDrone.add(linkTo(methodOn(DroneController.class).updateDrone(serialNumber, updatedDrone)).withSelfRel());
        return ResponseEntity.ok(updatedDrone);
    }

    @DeleteMapping("/{serialNumber}")
    @Operation(summary = "Delete a drone by serial number")
    @ApiResponse(responseCode = "204", description = "Successfully deleted drone")
    @ApiResponse(responseCode = "404", description = "Drone not found")
    @PreAuthorize("hasAuthority('" + Role.ADMIN + "')")
    public ResponseEntity<Void> deleteDrone(@PathVariable String serialNumber) {
        droneService.deleteDrone(serialNumber);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{serialNumber}/audit-logs")
    @Operation(summary = "Get audit logs for a drone")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved audit logs")
    @PreAuthorize("hasAuthority('" + Role.ADMIN + "')")
    public ResponseEntity<PageResponseDto<AuditLogDto>> getDroneAuditLogs(
            @PathVariable String serialNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponseDto<AuditLogDto> auditLogs = droneService.getDroneAuditLogs(serialNumber, page, size);
        auditLogs.getContent().forEach(auditLogDto ->
            auditLogDto.add(linkTo(methodOn(DroneController.class).getDroneAuditLogs(serialNumber, page, size)).withSelfRel()));
        auditLogs.add(linkTo(methodOn(DroneController.class).getDroneAuditLogs(serialNumber, page, size)).withSelfRel());
        return ResponseEntity.ok(auditLogs);
    }
}
