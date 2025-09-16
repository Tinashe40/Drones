package com.tinashe.dronesbackend.service;

import com.tinashe.dronesbackend.dto.MedicationDto;
import com.tinashe.dronesbackend.model.Medication;

public interface MedicationService {

    Medication findOrCreateMedication(MedicationDto medicationDto);
}