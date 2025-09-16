package com.tinashe.dronesbackend.scheduler;

import com.tinashe.dronesbackend.model.Drone;
import com.tinashe.dronesbackend.service.AuditService;
import com.tinashe.dronesbackend.service.DroneService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class DroneBatteryCheckScheduler {

    private final DroneService droneService;
    private final AuditService auditService;

    // Schedule to run every 5 minutes (300000 ms)
    @Scheduled(fixedRate = 300000)
    public void checkDroneBatteryLevels() {
        List<Drone> drones = droneService.getAllDrones();
        for (Drone drone : drones) {
            auditService.logBatteryEvent(drone.getSerialNumber(), drone.getBatteryCapacity());
            // In a real scenario, you might want to log this to a file or a more robust logging system
            System.out.println(String.format("Drone %s battery level: %d%%", drone.getSerialNumber(), drone.getBatteryCapacity()));
        }
    }
}
