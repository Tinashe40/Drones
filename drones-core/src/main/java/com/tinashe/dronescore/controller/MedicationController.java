package com.tinashe.dronescore.controller;

import com.tinashe.dronescore.dto.MedicationDto;
import com.tinashe.dronescore.dto.PageResponseDto;
import com.tinashe.dronescore.enums.Role;
import com.tinashe.dronescore.service.MedicationService;
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

@RestController
@RequestMapping("/api/medications")
@RequiredArgsConstructor
@Tag(name = "Medication Controller", description = "Endpoints for managing medications")
public class MedicationController {

    private final MedicationService medicationService;

    @PostMapping
    @Operation(summary = "Add a new medication")
    @ApiResponse(responseCode = "201", description = "Medication added successfully")
    @PreAuthorize("hasAuthority('" + Role.ADMIN + "')")
    public ResponseEntity<MedicationDto> addMedication(@Valid @RequestBody MedicationDto medicationDto) {
        MedicationDto newMedication = medicationService.addMedication(medicationDto);
        newMedication.add(linkTo(methodOn(MedicationController.class).getMedicationByCode(newMedication.getCode())).withSelfRel());
        return new ResponseEntity<>(newMedication, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all medications")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all medications")
    @PreAuthorize("hasAnyAuthority('" + Role.ADMIN + "', '" + Role.DISPATCHER + "', '" + Role.USER + "', '" + Role.MAINTENANCE + "')")
    public ResponseEntity<PageResponseDto<MedicationDto>> getAllMedications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponseDto<MedicationDto> medications = medicationService.getAllMedications(page, size);
        medications.getContent().forEach(medicationDto ->
            medicationDto.add(linkTo(methodOn(MedicationController.class).getMedicationByCode(medicationDto.getCode())).withSelfRel()));
        medications.add(linkTo(methodOn(MedicationController.class).getAllMedications(page, size)).withSelfRel());
        return ResponseEntity.ok(medications);
    }

    @GetMapping("/{code}")
    @Operation(summary = "Get a medication by code")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the medication")
    @PreAuthorize("hasAnyAuthority('" + Role.ADMIN + "', '" + Role.DISPATCHER + "', '" + Role.USER + "', '" + Role.MAINTENANCE + "')")
    public ResponseEntity<MedicationDto> getMedicationByCode(@PathVariable String code) {
        MedicationDto medication = medicationService.getMedicationByCode(code);
        medication.add(linkTo(methodOn(MedicationController.class).getMedicationByCode(code)).withSelfRel());
        medication.add(linkTo(methodOn(MedicationController.class).updateMedication(code, new MedicationDto())).withRel("update-medication"));
        medication.add(linkTo(methodOn(MedicationController.class).deleteMedication(code)).withRel("delete-medication"));
        return ResponseEntity.ok(medication);
    }

    @PutMapping("/{code}")
    @Operation(summary = "Update a medication")
    @ApiResponse(responseCode = "200", description = "Successfully updated the medication")
    @PreAuthorize("hasAuthority('" + Role.ADMIN + "')")
    public ResponseEntity<MedicationDto> updateMedication(@PathVariable String code, @Valid @RequestBody MedicationDto medicationDto) {
        MedicationDto updatedMedication = medicationService.updateMedication(code, medicationDto);
        updatedMedication.add(linkTo(methodOn(MedicationController.class).updateMedication(code, updatedMedication)).withSelfRel());
        return ResponseEntity.ok(updatedMedication);
    }

    @DeleteMapping("/{code}")
    @Operation(summary = "Delete a medication")
    @ApiResponse(responseCode = "204", description = "Successfully deleted the medication")
    @PreAuthorize("hasAuthority('" + Role.ADMIN + "')")
    public ResponseEntity<Void> deleteMedication(@PathVariable String code) {
        medicationService.deleteMedication(code);
        return ResponseEntity.noContent().build();
    }
}