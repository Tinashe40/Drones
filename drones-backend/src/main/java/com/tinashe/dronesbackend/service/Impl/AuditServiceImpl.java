package com.tinashe.dronesbackend.service.Impl;

import com.tinashe.dronesbackend.model.AuditLog;
import com.tinashe.dronesbackend.model.Drone;
import com.tinashe.dronesbackend.repository.AuditLogRepository;
import com.tinashe.dronesbackend.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;

    @Override
    public void logBatteryLevel(Drone drone) {
        log.info("Logging battery level for drone: {}", drone.getSerialNumber());
        AuditLog auditLog = new AuditLog(drone.getSerialNumber(), drone.getBatteryCapacity(), "BATTERY_LEVEL_CHECK");
        auditLogRepository.save(auditLog);
    }
}
