package com.tinashe.dronescore.service;

import com.tinashe.dronescore.model.Drone;
import com.tinashe.dronescore.enums.DroneState;

public interface AuditService {
    void logBatteryLevel(Drone drone, String userId, String details);
    void logDroneStateChange(Drone drone, String eventType, String userId, String details);
}
