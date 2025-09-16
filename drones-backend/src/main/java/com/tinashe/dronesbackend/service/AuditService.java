package com.tinashe.dronesbackend.service;

import com.tinashe.dronesbackend.model.AuditLog;
import com.tinashe.dronesbackend.repository.AuditLogRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void logBatteryEvent(String droneSerialNumber, int batteryCapacity) {
        AuditLog auditLog = new AuditLog();
        auditLog.setDroneSerialNumber(droneSerialNumber);
        auditLog.setBatteryCapacity(batteryCapacity);
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setEventType("BATTERY_CHECK");
        auditLogRepository.save(auditLog);
    }
}
