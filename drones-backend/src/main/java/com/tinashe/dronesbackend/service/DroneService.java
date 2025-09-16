package com.tinashe.dronesbackend.service;

import com.tinashe.dronesbackend.dto.DroneDto;
import com.tinashe.dronesbackend.dto.MedicationDto;
import com.tinashe.dronesbackend.exception.DroneException;
import com.tinashe.dronesbackend.model.Drone;
import com.tinashe.dronesbackend.model.DroneState;
import com.tinashe.dronesbackend.model.Medication;
import com.tinashe.dronesbackend.repository.DroneRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DroneService {

    private final DroneRepository droneRepository;
    private final MedicationService medicationService;

    public Drone registerDrone(DroneDto droneDto) {
        if (droneRepository.findBySerialNumber(droneDto.getSerialNumber()).isPresent()) {
            throw new DroneException("Drone with serial number " + droneDto.getSerialNumber() + " already exists.");
        }
        Drone drone = new Drone();
        drone.setSerialNumber(droneDto.getSerialNumber());
        drone.setModel(droneDto.getModel());
        drone.setWeightLimit(droneDto.getWeightLimit());
        drone.setBatteryCapacity(droneDto.getBatteryCapacity());
        drone.setState(DroneState.IDLE);
        return droneRepository.save(drone);
    }

    @Transactional
    public Drone loadMedications(String serialNumber, List<MedicationDto> medicationDtos) {
        Drone drone = droneRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new DroneException("Drone not found with serial number: " + serialNumber));

        if (drone.getState() != DroneState.IDLE && drone.getState() != DroneState.LOADING) {
            throw new DroneException("Drone is not in IDLE or LOADING state. Current state: " + drone.getState());
        }

        if (drone.getBatteryCapacity() < 25) {
            throw new DroneException("Drone battery level is below 25% (" + drone.getBatteryCapacity() + "%). Cannot load.");
        }

        double totalMedicationWeight = medicationDtos.stream().mapToDouble(MedicationDto::getWeight).sum();
        double currentLoadedWeight = drone.getMedications() != null ? drone.getMedications().stream().mapToDouble(Medication::getWeight).sum() : 0;

        if (currentLoadedWeight + totalMedicationWeight > drone.getWeightLimit()) {
            throw new DroneException("Total weight of medications (" + (currentLoadedWeight + totalMedicationWeight) + "gr) exceeds drone's weight limit (" + drone.getWeightLimit() + "gr).");
        }

        drone.setState(DroneState.LOADING);
        droneRepository.save(drone); // Update state to LOADING

        List<Medication> medicationsToLoad = medicationDtos.stream()
                .map(medicationService::findOrCreateMedication)
                .collect(Collectors.toList());

        if (drone.getMedications() == null) {
            drone.setMedications(medicationsToLoad);
        } else {
            drone.getMedications().addAll(medicationsToLoad);
        }

        drone.setState(DroneState.LOADED);
        return droneRepository.save(drone);
    }

    public List<MedicationDto> getLoadedMedications(String serialNumber) {
        Drone drone = droneRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new DroneException("Drone not found with serial number: " + serialNumber));

        return drone.getMedications().stream()
                .map(medication -> {
                    MedicationDto dto = new MedicationDto();
                    dto.setName(medication.getName());
                    dto.setWeight(medication.getWeight());
                    dto.setCode(medication.getCode());
                    dto.setImageUrl(medication.getImageUrl());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<DroneDto> getAvailableDronesForLoading() {
        return droneRepository.findByStateAndBatteryCapacityGreaterThanEqual(DroneState.IDLE, 25).stream()
                .map(drone -> {
                    DroneDto dto = new DroneDto();
                    dto.setSerialNumber(drone.getSerialNumber());
                    dto.setModel(drone.getModel());
                    dto.setWeightLimit(drone.getWeightLimit());
                    dto.setBatteryCapacity(drone.getBatteryCapacity());
                    dto.setState(drone.getState());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public int getDroneBatteryLevel(String serialNumber) {
        Drone drone = droneRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new DroneException("Drone not found with serial number: " + serialNumber));
        return drone.getBatteryCapacity();
    }

    public Optional<Drone> findBySerialNumber(String serialNumber) {
        return droneRepository.findBySerialNumber(serialNumber);
    }

    public List<Drone> getAllDrones() {
        return droneRepository.findAll();
    }

    public Drone updateDrone(Drone drone) {
        return droneRepository.save(drone);
    }
}
