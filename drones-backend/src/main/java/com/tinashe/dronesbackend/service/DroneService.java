package com.tinashe.dronesbackend.service;

import com.tinashe.dronesbackend.dto.DroneDto;
import com.tinashe.dronesbackend.dto.LoadMedicationRequest;
import com.tinashe.dronesbackend.dto.MedicationDto;

import java.util.List;

public interface DroneService {

    DroneDto registerDrone(DroneDto droneDto);

    void loadMedication(String serialNumber, LoadMedicationRequest request);

    List<MedicationDto> getLoadedMedication(String serialNumber);

    List<DroneDto> getAvailableDrones();

    int getDroneBatteryLevel(String serialNumber);
}
