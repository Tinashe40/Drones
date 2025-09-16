package com.tinashe.dronesbackend.service.Impl;

import com.tinashe.dronesbackend.model.AuditLog;
import com.tinashe.dronesbackend.model.Drone;
import com.tinashe.dronesbackend.repository.AuditLogRepository;
import com.tinashe.dronesbackend.enums.DroneModel;
import com.tinashe.dronesbackend.enums.DroneState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditServiceImpl auditService;

    private Drone drone;

    @BeforeEach
    void setUp() {
        drone = new Drone();
        drone.setId(1L);
        drone.setSerialNumber("DRN001");
        drone.setModel(DroneModel.LIGHTWEIGHT);
        drone.setWeightLimit(100);
        drone.setBatteryCapacity(90);
        drone.setState(DroneState.IDLE);
    }

    @Test
    void logBatteryLevel_success() {
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        auditService.logBatteryLevel(drone);

        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }
}
