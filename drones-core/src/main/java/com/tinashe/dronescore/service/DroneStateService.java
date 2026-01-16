package com.tinashe.dronescore.service;

import com.tinashe.dronescore.model.Drone;
import com.tinashe.dronescore.enums.DroneState;

public interface DroneStateService {
    Drone initiateLoading(String serialNumber);
    Drone completeLoading(String serialNumber);
    Drone dispatchDrone(String serialNumber);
    Drone deliverMedication(String serialNumber);
    Drone returnDrone(String serialNumber);
    Drone startCharging(String serialNumber);
    Drone completeCharging(String serialNumber);
    Drone unloadDrone(String serialNumber);
    // Potentially more generic state change method if needed
    Drone changeDroneState(String serialNumber, DroneState newState);
}