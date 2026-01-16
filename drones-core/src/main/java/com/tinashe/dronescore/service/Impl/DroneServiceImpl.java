package com.tinashe.dronescore.service.Impl;

import com.tinashe.dronescore.dto.DroneDto;
import com.tinashe.dronescore.dto.LoadMedicationRequest;
import com.tinashe.dronescore.dto.MedicationDto;
import com.tinashe.dronescore.dto.PageResponseDto;
import com.tinashe.dronescore.enums.MedicationStatus;
import com.tinashe.dronescore.exception.DroneException;
import com.tinashe.dronescore.exception.DroneNotFoundException;
import com.tinashe.dronescore.exception.LowBatteryException;
import com.tinashe.dronescore.exception.WeightLimitExceededException;
import com.tinashe.dronescore.model.Drone;
import com.tinashe.dronescore.enums.DroneState;
import com.tinashe.dronescore.model.Medication;
import com.tinashe.dronescore.repository.AuditLogRepository;
import com.tinashe.dronescore.repository.DroneRepository;
import com.tinashe.dronescore.repository.MedicationRepository;
import com.tinashe.dronescore.service.DroneService;
import com.tinashe.dronescore.service.DroneStateService;
import com.tinashe.dronescore.service.MedicationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
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
    private final AuditLogRepository auditLogRepository;
    private final DroneStateService droneStateService;
    private final MedicationService medicationService;

    // Placeholder constants for prediction calculations
    private static final double HOVER_CONSUMPTION_RATE_PERCENT_PER_MINUTE = 0.5; // % battery / minute
    private static final double CHARGE_RATE_PERCENT_PER_MINUTE = 1.0; // % battery / minute
    private static final double FLIGHT_DISTANCE_PER_PERCENT_BATTERY_KM = 0.2; // km / %battery (baseline)
    private static final double WEIGHT_IMPACT_FACTOR = 0.001; // For every gram over baseline, reduce efficiency

    @Override
    public DroneDto registerDrone(DroneDto droneDto) {
        Drone drone = modelMapper.map(droneDto, Drone.class);
        Drone registeredDrone = droneRepository.save(drone);
        return modelMapper.map(registeredDrone, DroneDto.class);
    }

    @Override
    public DroneDto initiateLoading(String serialNumber) {
        Drone drone = droneStateService.initiateLoading(serialNumber);
        return modelMapper.map(drone, DroneDto.class);
    }

    @Override
    public DroneDto completeLoading(String serialNumber) {
        Drone drone = droneStateService.completeLoading(serialNumber);
        return modelMapper.map(drone, DroneDto.class);
    }

    @Override
    public DroneDto dispatchDrone(String serialNumber) {
        Drone drone = droneStateService.dispatchDrone(serialNumber);
        // Update status of medications on drone to IN_TRANSIT
        Drone existingDrone = droneRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new DroneNotFoundException("Drone not found with serial number: " + serialNumber));
        existingDrone.getMedications().forEach(med -> medicationService.updateMedicationStatus(med.getCode(), MedicationStatus.IN_TRANSIT));
        return modelMapper.map(drone, DroneDto.class);
    }

    @Override
    public DroneDto deliverMedication(String serialNumber) {
        Drone drone = droneStateService.deliverMedication(serialNumber);
        // Update status of medications on drone to DELIVERED
        Drone existingDrone = droneRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new DroneNotFoundException("Drone not found with serial number: " + serialNumber));
        existingDrone.getMedications().forEach(med -> medicationService.updateMedicationStatus(med.getCode(), MedicationStatus.DELIVERED));
        return modelMapper.map(drone, DroneDto.class);
    }

    @Override
    public DroneDto returnDrone(String serialNumber) {
        Drone drone = droneStateService.returnDrone(serialNumber);
        return modelMapper.map(drone, DroneDto.class);
    }

    @Override
    public DroneDto startCharging(String serialNumber) {
        Drone drone = droneStateService.startCharging(serialNumber);
        return modelMapper.map(drone, DroneDto.class);
    }

    @Override
    public DroneDto completeCharging(String serialNumber) {
        Drone drone = droneStateService.completeCharging(serialNumber);
        return modelMapper.map(drone, DroneDto.class);
    }

    @Override
    public DroneDto unloadDrone(String serialNumber) {
        Drone drone = droneStateService.unloadDrone(serialNumber);
        // Update status of medications on drone to UNLOADED/AVAILABLE and return quantity
        Drone existingDrone = droneRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new DroneNotFoundException("Drone not found with serial number: " + serialNumber));
        existingDrone.getMedications().forEach(med -> {
            medicationService.updateMedicationStatus(med.getCode(), MedicationStatus.AVAILABLE);
            medicationService.incrementMedicationQuantity(med.getCode(), 1); // Assuming 1 quantity per medication item
        });
        existingDrone.getMedications().clear(); // Clear medications from drone after unloading
        droneRepository.save(existingDrone);
        return modelMapper.map(drone, DroneDto.class);
    }

    @Override
    @Async
    public void loadMedication(String serialNumber, LoadMedicationRequest request) {
        Drone drone = droneRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new DroneNotFoundException("Drone not found with serial number: " + serialNumber));

        if (drone.getBatteryCapacity() < 25) {
            throw new LowBatteryException("Drone battery level is below 25%");
        }

        List<Medication> medicationsToLoad = medicationRepository.findByCodeIn(request.getMedicationCodes());
        if (medicationsToLoad.size() != request.getMedicationCodes().size()) {
            throw new DroneException("One or more medications not found");
        }

        int totalWeightToAdd = medicationsToLoad.stream().mapToInt(Medication::getWeight).sum();
        int currentWeight = drone.getMedications().stream().mapToInt(Medication::getWeight).sum();

        if (currentWeight + totalWeightToAdd > drone.getWeightLimit()) {
            throw new WeightLimitExceededException("Weight limit exceeded for the drone");
        }

        // Check medication availability and decrement quantity
        medicationsToLoad.forEach(med -> {
            if (med.getQuantity() < 1) {
                throw new DroneException("Medication " + med.getCode() + " is out of stock.");
            }
            medicationService.decrementMedicationQuantity(med.getCode(), 1);
            medicationService.updateMedicationStatus(med.getCode(), MedicationStatus.LOADED);
        });

        drone.getMedications().addAll(medicationsToLoad);
        droneRepository.save(drone);

        // State transition to LOADED now happens after successful medication loading
        // droneStateService.completeLoading(serialNumber);
    }


    @Override
    public PageResponseDto<MedicationDto> getLoadedMedication(String serialNumber, int page, int size) {
        Drone drone = droneRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new DroneNotFoundException("Drone not found with serial number: " + serialNumber));

        Pageable pageable = PageRequest.of(page, size);
        List<String> medicationCodes = drone.getMedications().stream()
                                            .map(Medication::getCode)
                                            .collect(Collectors.toList());

        Page<Medication> medicationPage = medicationRepository.findByCodeIn(medicationCodes, pageable);

        return new PageResponseDto<>(medicationPage.map(medication -> modelMapper.map(medication, MedicationDto.class)));
    }

    @Override
    public PageResponseDto<DroneDto> getAvailableDrones(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Drone> availableDrones = droneRepository.findByStateAndBatteryCapacityGreaterThanEqual(DroneState.IDLE, 25, pageable);
        return new PageResponseDto<>(availableDrones.map(drone -> modelMapper.map(drone, DroneDto.class)));
    }

    @Override
    public int getDroneBatteryLevel(String serialNumber) {
        Drone drone = droneRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new DroneNotFoundException("Drone not found with serial number: " + serialNumber));
        return drone.getBatteryCapacity();
    }

    @Override
    public DroneDto getDroneBySerialNumber(String serialNumber) {
        Drone drone = droneRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new DroneNotFoundException("Drone not found with serial number: " + serialNumber));
        DroneDto droneDto = modelMapper.map(drone, DroneDto.class);
        // Populate prediction fields
        droneDto.setPredictedFlightTimeMinutes(calculatePredictedFlightTime(serialNumber));
        droneDto.setPredictedRangeKm(calculatePredictedRange(serialNumber));
        droneDto.setPredictedChargingTimeMinutes(calculatePredictedChargingTime(serialNumber));
        return droneDto;
    }

    @Override
    public DroneDto updateDrone(String serialNumber, DroneDto droneDto) {
        Drone existingDrone = droneRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new DroneNotFoundException("Drone not found with serial number: " + serialNumber));

        // Update fields that are allowed to be changed
        existingDrone.setBatteryCapacity(droneDto.getBatteryCapacity());
        // Delegate state changes to DroneStateService to ensure valid transitions
        if (existingDrone.getState() != droneDto.getState()) {
             droneStateService.changeDroneState(serialNumber, droneDto.getState(), "MANUAL_STATE_UPDATE");
        }

        Drone updatedDrone = droneRepository.save(existingDrone);
        return modelMapper.map(updatedDrone, DroneDto.class);
    }

    @Override
    public void deleteDrone(String serialNumber) {
        Drone drone = droneRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new DroneNotFoundException("Drone not found with serial number: " + serialNumber));
        droneRepository.delete(drone);
    }

    @Override
    public PageResponseDto<com.tinashe.dronescore.dto.AuditLogDto> getDroneAuditLogs(String serialNumber, int page, int size) {
        // Ensure the drone exists before trying to get its logs
        droneRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new DroneNotFoundException("Drone not found with serial number: " + serialNumber));

        Pageable pageable = PageRequest.of(page, size);
        Page<com.tinashe.dronescore.model.AuditLog> auditLogPage = auditLogRepository.findByDroneSerialNumber(serialNumber, pageable);

        return new PageResponseDto<>(auditLogPage.map(auditLog -> modelMapper.map(auditLog, com.tinashe.dronescore.dto.AuditLogDto.class)));
    }

    @Override
    public PageResponseDto<DroneDto> getAllDrones(DroneState state, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Drone> dronePage;

        if (state != null) {
            dronePage = droneRepository.findAllByState(state, pageable);
        } else {
            dronePage = droneRepository.findAll(pageable);
        }
        return new PageResponseDto<>(dronePage.map(drone -> modelMapper.map(drone, DroneDto.class)));
    }

    // Prediction calculation methods
    @Override
    public Integer calculatePredictedFlightTime(String serialNumber) {
        Drone drone = droneRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new DroneNotFoundException("Drone not found with serial number: " + serialNumber));

        int currentWeight = drone.getMedications().stream().mapToInt(Medication::getWeight).sum();
        double effectiveConsumptionRate = HOVER_CONSUMPTION_RATE_PERCENT_PER_MINUTE * (1 + (currentWeight * WEIGHT_IMPACT_FACTOR));
        
        // Time = currentBattery / consumptionRate
        int predictedTime = (int) (drone.getBatteryCapacity() / effectiveConsumptionRate);
        return Math.max(0, predictedTime); // Ensure not negative
    }

    @Override
    public Integer calculatePredictedRange(String serialNumber) {
        Drone drone = droneRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new DroneNotFoundException("Drone not found with serial number: " + serialNumber));
        
        int currentWeight = drone.getMedications().stream().mapToInt(Medication::getWeight).sum();
        double effectiveDistancePerPercent = FLIGHT_DISTANCE_PER_PERCENT_BATTERY_KM * (1 - (currentWeight * WEIGHT_IMPACT_FACTOR));
        
        int predictedRange = (int) (drone.getBatteryCapacity() * effectiveDistancePerPercent);
        return Math.max(0, predictedRange); // Ensure not negative
    }

    @Override
    public Integer calculatePredictedChargingTime(String serialNumber) {
        Drone drone = droneRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new DroneNotFoundException("Drone not found with serial number: " + serialNumber));

        int neededCharge = 100 - drone.getBatteryCapacity();
        if (neededCharge <= 0) {
            return 0; // Already fully charged
        }
        int predictedChargeTime = (int) (neededCharge / CHARGE_RATE_PERCENT_PER_MINUTE);
        return Math.max(0, predictedChargeTime); // Ensure not negative
    }

    @Override
    public DroneDto getDronePredictions(String serialNumber) {
        DroneDto droneDto = getDroneBySerialNumber(serialNumber); // Reuse existing method to get base DTO
        // Predictions are already populated in getDroneBySerialNumber
        return droneDto;
    }
}