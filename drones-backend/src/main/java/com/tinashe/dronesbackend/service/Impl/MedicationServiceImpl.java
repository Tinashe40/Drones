package com.tinashe.dronesbackend.service.Impl;

import com.tinashe.dronesbackend.dto.MedicationDto;
import com.tinashe.dronesbackend.model.Medication;
import com.tinashe.dronesbackend.repository.MedicationRepository;
import com.tinashe.dronesbackend.service.MedicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MedicationServiceImpl implements MedicationService {

    private final MedicationRepository medicationRepository;

    @Override
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