package com.tinashe.dronescore.service.Impl;

import com.tinashe.dronescore.enums.DroneState;
import com.tinashe.dronescore.exception.DroneException;
import com.tinashe.dronescore.exception.DroneNotFoundException;
import com.tinashe.dronescore.model.Drone;
import com.tinashe.dronescore.repository.DroneRepository;
import com.tinashe.dronescore.service.AuditService;
import com.tinashe.dronescore.service.DroneStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DroneStateServiceImpl implements DroneStateService {

    private final DroneRepository droneRepository;
    private final AuditService auditService; // To log state changes

    private Drone findDrone(String serialNumber) {
        return droneRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new DroneNotFoundException("Drone not found with serial number: " + serialNumber));
    }

    private Drone updateDroneState(String serialNumber, DroneState expectedCurrentState, DroneState newState, String eventType, String details) {
        Drone drone = findDrone(serialNumber);
        if (drone.getState() != expectedCurrentState) {
            throw new DroneException(String.format("Drone with serial number %s is not in %s state, but in %s state.",
                    serialNumber, expectedCurrentState, drone.getState()));
        }
        drone.setState(newState);
        Drone updatedDrone = droneRepository.save(drone);
        auditService.logDroneStateChange(updatedDrone, eventType, null, details); // Log the state change
        return updatedDrone;
    }

    private Drone updateDroneState(String serialNumber, DroneState newState, String eventType, String details) {
        Drone drone = findDrone(serialNumber);
        drone.setState(newState);
        Drone updatedDrone = droneRepository.save(drone);
        auditService.logDroneStateChange(updatedDrone, eventType, null, details); // Log the state change
        return updatedDrone;
    }

    @Override
    public Drone initiateLoading(String serialNumber) {
        return updateDroneState(serialNumber, DroneState.IDLE, DroneState.LOADING, "INITIATE_LOADING", "Initiating loading process.");
    }

    @Override
    public Drone completeLoading(String serialNumber) {
        return updateDroneState(serialNumber, DroneState.LOADING, DroneState.LOADED, "COMPLETE_LOADING", "Loading process completed.");
    }

    @Override
    public Drone dispatchDrone(String serialNumber) {
        return updateDroneState(serialNumber, DroneState.LOADED, DroneState.DELIVERING, "DISPATCH_DRONE", "Drone dispatched for delivery.");
    }

    @Override
    public Drone deliverMedication(String serialNumber) {
        return updateDroneState(serialNumber, DroneState.DELIVERING, DroneState.DELIVERED, "DELIVER_MEDICATION", "Medication delivered.");
    }

    @Override
    public Drone returnDrone(String serialNumber) {
        return updateDroneState(serialNumber, DroneState.DELIVERED, DroneState.RETURNING, "RETURN_DRONE", "Drone returning to base.");
    }

    @Override
    public Drone startCharging(String serialNumber) {
        // Can charge from IDLE or RETURNING (after delivered)
        Drone drone = findDrone(serialNumber);
        if (drone.getState() != DroneState.IDLE && drone.getState() != DroneState.RETURNING) {
            throw new DroneException(String.format("Drone with serial number %s cannot start charging from %s state.",
                    serialNumber, drone.getState()));
        }
        drone.setState(DroneState.CHARGING);
        Drone updatedDrone = droneRepository.save(drone);
        auditService.logDroneStateChange(updatedDrone, "START_CHARGING", null, "Drone started charging.");
        return updatedDrone;
    }

    @Override
    public Drone completeCharging(String serialNumber) {
        return updateDroneState(serialNumber, DroneState.CHARGING, DroneState.IDLE, "COMPLETE_CHARGING", "Charging completed.");
    }

    @Override
    public Drone unloadDrone(String serialNumber) {
        // Unloading typically happens after DELIVERED (at base) or if delivery is cancelled.
        // For simplicity, let's assume it transitions from DELIVERED to IDLE or if it's LOADED and needs to be returned to IDLE
        Drone drone = findDrone(serialNumber);
        if (drone.getState() == DroneState.LOADED || drone.getState() == DroneState.DELIVERED) {
            drone.setState(DroneState.IDLE);
            Drone updatedDrone = droneRepository.save(drone);
            auditService.logDroneStateChange(updatedDrone, "UNLOAD_DRONE", null, "Medications unloaded from drone.");
            return updatedDrone;
        } else {
            throw new DroneException(String.format("Drone with serial number %s cannot be unloaded from %s state.",
                    serialNumber, drone.getState()));
        }
    }

    @Override
    public Drone changeDroneState(String serialNumber, DroneState newState) {
        // This is a more generic state change, useful for exceptional cases or initial setup, but
        // specific methods are preferred for business logic.
        return updateDroneState(serialNumber, newState, "GENERIC_STATE_CHANGE_TO_" + newState.name(), "Manual state override.");
    }
}
