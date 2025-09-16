package com.tinashe.dronesbackend.service;

import com.tinashe.dronesbackend.dto.MedicationDto;
import com.tinashe.dronesbackend.model.Medication;
import com.tinashe.dronesbackend.repository.MedicationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MedicationService {

    private final MedicationRepository medicationRepository;

    public Medication findOrCreateMedication(MedicationDto medicationDto) {
        return medicationRepository.findByCode(medicationDto.getCode())
                .orElseGet(() -> {
                    Medication newMedication = new Medication();
                    newMedication.setName(medicationDto.getName());
                    newMedication.setWeight(medicationDto.getWeight());
                    newMedication.setCode(medicationDto.getCode());
                    newMedication.setImageUrl(medicationDto.getImageUrl());
                    return medicationRepository.save(newMedication);
                });
    }
}
