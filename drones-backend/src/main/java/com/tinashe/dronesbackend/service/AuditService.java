package com.tinashe.dronesbackend.service;

import com.tinashe.dronesbackend.model.Drone;

public interface AuditService {
    void logBatteryLevel(Drone drone);
}
