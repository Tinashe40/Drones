package com.tinashe.dronesbackend.service.Impl;

import com.tinashe.dronesbackend.dto.DroneDto;
import com.tinashe.dronesbackend.dto.LoadMedicationRequest;
import com.tinashe.dronesbackend.dto.MedicationDto;
import com.tinashe.dronesbackend.exception.DroneException;
import com.tinashe.dronesbackend.exception.DroneNotFoundException;
import com.tinashe.dronesbackend.exception.LowBatteryException;
import com.tinashe.dronesbackend.exception.WeightLimitExceededException;
import com.tinashe.dronesbackend.model.Drone;
import com.tinashe.dronesbackend.enums.DroneState;
import com.tinashe.dronesbackend.model.Medication;
import com.tinashe.dronesbackend.repository.DroneRepository;
import com.tinashe.dronesbackend.repository.MedicationRepository;
import com.tinashe.dronesbackend.service.DroneService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DroneServiceImpl implements DroneService {

    private final DroneRepository droneRepository;
    private final MedicationRepository medicationRepository;
    private final ModelMapper modelMapper;

    @Override
    public DroneDto registerDrone(DroneDto droneDto) {
        Drone drone = modelMapper.map(droneDto, Drone.class);
        Drone registeredDrone = droneRepository.save(drone);
        return modelMapper.map(registeredDrone, DroneDto.class);
    }

    @Override
    public void loadMedication(String serialNumber, LoadMedicationRequest request) {
        Drone drone = droneRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new DroneNotFoundException("Drone not found with serial number: " + serialNumber));

        if (drone.getBatteryCapacity() < 25) {
            throw new LowBatteryException("Drone battery level is below 25%");
        }

        if (drone.getState() != DroneState.IDLE && drone.getState() != DroneState.LOADING) {
            throw new DroneException("Drone is not in IDLE or LOADING state");
        }

        List<Medication> medications = medicationRepository.findByCodeIn(request.getMedicationCodes());
        if (medications.size() != request.getMedicationCodes().size()) {
            throw new DroneException("One or more medications not found");
        }

        int totalWeight = medications.stream().mapToInt(Medication::getWeight).sum();
        int currentWeight = drone.getMedications().stream().mapToInt(Medication::getWeight).sum();

        if (currentWeight + totalWeight > drone.getWeightLimit()) {
            throw new WeightLimitExceededException("Weight limit exceeded for the drone");
        }

        drone.getMedications().addAll(medications);
        drone.setState(DroneState.LOADING);
        droneRepository.save(drone);
    }

    @Override
    public List<MedicationDto> getLoadedMedication(String serialNumber) {
        Drone drone = droneRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new DroneNotFoundException("Drone not found with serial number: " + serialNumber));

        return drone.getMedications().stream()
                .map(medication -> modelMapper.map(medication, MedicationDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<DroneDto> getAvailableDrones() {
        List<Drone> availableDrones = droneRepository.findByStateAndBatteryCapacityGreaterThanEqual(DroneState.IDLE, 25);
        return availableDrones.stream()
                .map(drone -> modelMapper.map(drone, DroneDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public int getDroneBatteryLevel(String serialNumber) {
        Drone drone = droneRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new DroneNotFoundException("Drone not found with serial number: " + serialNumber));
        return drone.getBatteryCapacity();
    }
}