package com.tinashe.dronescore.service.Impl;

import com.tinashe.dronescore.model.AuditLog;
import com.tinashe.dronescore.model.Drone;
import com.tinashe.dronescore.repository.AuditLogRepository;
import com.tinashe.dronescore.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)) {
            return authentication.getName();
        }
        return "SYSTEM"; // Default or anonymous user
    }

    @Override
    public void logBatteryLevel(Drone drone, String userId, String details) {
        String actualUserId = (userId != null && !userId.isEmpty()) ? userId : getCurrentUserId();
        log.info("Logging battery level for drone {}: User = {}, Details = {}", drone.getSerialNumber(), actualUserId, details);
        AuditLog auditLog = new AuditLog(drone.getSerialNumber(), drone.getBatteryCapacity(), "BATTERY_LEVEL_CHECK", actualUserId, details);
        auditLogRepository.save(auditLog);
    }

    @Override
    public void logDroneStateChange(Drone drone, String eventType, String userId, String details) {
        String actualUserId = (userId != null && !userId.isEmpty()) ? userId : getCurrentUserId();
        log.info("Logging state change for drone {}: New State = {}, Event = {}, User = {}, Details = {}",
                drone.getSerialNumber(), drone.getState(), eventType, actualUserId, details);
        AuditLog auditLog = new AuditLog(drone.getSerialNumber(), drone.getBatteryCapacity(), eventType + "_" + drone.getState().name(), actualUserId, details);
        auditLogRepository.save(auditLog);
    }
}
