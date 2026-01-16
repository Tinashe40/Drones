package com.tinashe.dronescore.service.Impl;

import com.tinashe.dronescore.dto.MedicationDto;
import com.tinashe.dronescore.dto.PageResponseDto;
import com.tinashe.dronescore.enums.MedicationStatus;
import com.tinashe.dronescore.exception.MedicationNotFoundException;
import com.tinashe.dronescore.model.Medication;
import com.tinashe.dronescore.repository.MedicationRepository;
import com.tinashe.dronescore.service.MedicationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicationServiceImpl implements MedicationService {

    private final MedicationRepository medicationRepository;
    private final ModelMapper modelMapper;

    @Override
    public Medication findOrCreateMedication(MedicationDto medicationDto) {
        return medicationRepository.findByCode(medicationDto.getCode())
                .orElseGet(() -> {
                    Medication newMedication = new Medication();
                    newMedication.setName(medicationDto.getName());
                    newMedication.setWeight(medicationDto.getWeight());
                    newMedication.setCode(medicationDto.getCode());
                    newMedication.setImageUrl(medicationDto.getImageUrl());
                    newMedication.setQuantity(medicationDto.getQuantity()); // Initialize quantity
                    newMedication.setMedicationStatus(MedicationStatus.AVAILABLE); // Default status
                    return medicationRepository.save(newMedication);
                });
    }

    @Override
    public MedicationDto addMedication(MedicationDto medicationDto) {
        Medication medication = modelMapper.map(medicationDto, Medication.class);
        if (medication.getQuantity() == 0) { // Ensure quantity is set for new meds
            medication.setQuantity(1);
        }
        if (medication.getMedicationStatus() == null) { // Default status if not provided
            medication.setMedicationStatus(MedicationStatus.AVAILABLE);
        }
        Medication savedMedication = medicationRepository.save(medication);
        return modelMapper.map(savedMedication, MedicationDto.class);
    }

    @Override
    public PageResponseDto<MedicationDto> getAllMedications(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Medication> medicationPage = medicationRepository.findAll(pageable);
        return new PageResponseDto<>(medicationPage.map(medication -> modelMapper.map(medication, MedicationDto.class)));
    }

    @Override
    public MedicationDto getMedicationByCode(String code) {
        Medication medication = medicationRepository.findByCode(code)
                .orElseThrow(() -> new MedicationNotFoundException("Medication not found with code: " + code));
        return modelMapper.map(medication, MedicationDto.class);
    }

    @Override
    public MedicationDto updateMedication(String code, MedicationDto medicationDto) {
        Medication existingMedication = medicationRepository.findByCode(code)
                .orElseThrow(() -> new MedicationNotFoundException("Medication not found with code: " + code));

        existingMedication.setName(medicationDto.getName());
        existingMedication.setWeight(medicationDto.getWeight());
        existingMedication.setImageUrl(medicationDto.getImageUrl());
        existingMedication.setQuantity(medicationDto.getQuantity()); // Update quantity
        existingMedication.setMedicationStatus(medicationDto.getMedicationStatus()); // Update status

        Medication updatedMedication = medicationRepository.save(existingMedication);
        return modelMapper.map(updatedMedication, MedicationDto.class);
    }

    @Override
    public void deleteMedication(String code) {
        Medication medication = medicationRepository.findByCode(code)
                .orElseThrow(() -> new MedicationNotFoundException("Medication not found with code: " + code));
        medicationRepository.delete(medication);
    }

    @Override
    public void decrementMedicationQuantity(String code, int amount) {
        Medication medication = medicationRepository.findByCode(code)
                .orElseThrow(() -> new MedicationNotFoundException("Medication not found with code: " + code));
        if (medication.getQuantity() < amount) {
            throw new RuntimeException("Not enough medication in stock for code: " + code);
        }
        medication.setQuantity(medication.getQuantity() - amount);
        medicationRepository.save(medication);
    }

    @Override
    public void incrementMedicationQuantity(String code, int amount) {
        Medication medication = medicationRepository.findByCode(code)
                .orElseThrow(() -> new MedicationNotFoundException("Medication not found with code: " + code));
        medication.setQuantity(medication.getQuantity() + amount);
        medicationRepository.save(medication);
    }

    @Override
    public void updateMedicationStatus(String code, MedicationStatus status) {
        Medication medication = medicationRepository.findByCode(code)
                .orElseThrow(() -> new MedicationNotFoundException("Medication not found with code: " + code));
        medication.setMedicationStatus(status);
        medicationRepository.save(medication);
    }
}
