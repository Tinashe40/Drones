package com.tinashe.dronescore.repository;

import com.tinashe.dronescore.common.jpa.BaseDao;
import com.tinashe.dronescore.model.Drone;
import com.tinashe.dronescore.enums.DroneState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DroneRepository extends BaseDao<Drone> {
    Optional<Drone> findBySerialNumber(String serialNumber);
    Page<Drone> findByStateAndBatteryCapacityGreaterThanEqual(DroneState state, int batteryCapacity, Pageable pageable);
    Page<Drone> findAllByState(DroneState state, Pageable pageable);
}
