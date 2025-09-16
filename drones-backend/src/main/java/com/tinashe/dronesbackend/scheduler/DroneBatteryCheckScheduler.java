package com.tinashe.dronesbackend.scheduler;

import com.tinashe.dronesbackend.enums.DroneState;
import com.tinashe.dronesbackend.model.Drone;
import com.tinashe.dronesbackend.repository.DroneRepository;
import com.tinashe.dronesbackend.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DroneBatteryCheckScheduler {

    private final DroneRepository droneRepository;
    private final AuditService auditService;

    @Scheduled(fixedRate = 300000) // 5 minutes
    public void checkBatteryLevels() {
        log.info("Running scheduled task to check drone battery levels...");
        List<Drone> drones = droneRepository.findAll();
        drones.forEach(drone -> {
            auditService.logBatteryLevel(drone);
            // Simulate battery drain for non-idle drones
            if (drone.getState() != DroneState.IDLE) {
                int currentBattery = drone.getBatteryCapacity();
                if (currentBattery > 0) {
                    drone.setBatteryCapacity(currentBattery - 1);
                    droneRepository.save(drone);
                }
            }
        });
        log.info("Finished checking drone battery levels.");
    }
}