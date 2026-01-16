package com.tinashe.dronescore.service;

import com.tinashe.dronescore.dto.MedicationDto;
import com.tinashe.dronescore.dto.PageResponseDto;
import com.tinashe.dronescore.enums.MedicationStatus;
import com.tinashe.dronescore.model.Medication;

import java.util.List;

public interface MedicationService {

    Medication findOrCreateMedication(MedicationDto medicationDto);

    MedicationDto addMedication(MedicationDto medicationDto);

    PageResponseDto<MedicationDto> getAllMedications(int page, int size);

    MedicationDto getMedicationByCode(String code);

    MedicationDto updateMedication(String code, MedicationDto medicationDto);

    void deleteMedication(String code);

    void decrementMedicationQuantity(String code, int amount);
    void incrementMedicationQuantity(String code, int amount);
    void updateMedicationStatus(String code, MedicationStatus status);
}