package com.tinashe.dronescore.service;

import com.tinashe.dronescore.dto.DroneDto;
import com.tinashe.dronescore.dto.LoadMedicationRequest;
import com.tinashe.dronescore.dto.MedicationDto;

import com.tinashe.dronescore.enums.DroneState;
import com.tinashe.dronescore.dto.PageResponseDto;

import java.util.List;

public interface DroneService {

    DroneDto registerDrone(DroneDto droneDto);
    // Modified to return DroneDto and call DroneStateService
    DroneDto initiateLoading(String serialNumber);
    DroneDto completeLoading(String serialNumber);
    DroneDto dispatchDrone(String serialNumber);
    DroneDto deliverMedication(String serialNumber);
    DroneDto returnDrone(String serialNumber);
    DroneDto startCharging(String serialNumber);
    DroneDto completeCharging(String serialNumber);
    DroneDto unloadDrone(String serialNumber);

    void loadMedication(String serialNumber, LoadMedicationRequest request); // Return type changed to void
    PageResponseDto<MedicationDto> getLoadedMedication(String serialNumber, int page, int size);
    PageResponseDto<DroneDto> getAvailableDrones(int page, int size);
    int getDroneBatteryLevel(String serialNumber);
    DroneDto getDroneBySerialNumber(String serialNumber);
    DroneDto updateDrone(String serialNumber, DroneDto droneDto);
    void deleteDrone(String serialNumber);
    PageResponseDto<com.tinashe.dronescore.dto.AuditLogDto> getDroneAuditLogs(String serialNumber, int page, int size);
    PageResponseDto<DroneDto> getAllDrones(DroneState state, int page, int size);

    // New methods for predictions
    Integer calculatePredictedFlightTime(String serialNumber);
    Integer calculatePredictedRange(String serialNumber);
    Integer calculatePredictedChargingTime(String serialNumber);
    DroneDto getDronePredictions(String serialNumber);

}
