package com.tinashe.dronesbackend.repository;

import com.tinashe.dronesbackend.common.BaseDao;
import com.tinashe.dronesbackend.model.Drone;
import com.tinashe.dronesbackend.enums.DroneState;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DroneRepository extends BaseDao<Drone> {
    Optional<Drone> findBySerialNumber(String serialNumber);
    List<Drone> findByStateAndBatteryCapacityGreaterThanEqual(DroneState state, int batteryCapacity);
    List<Drone> findAllByState(DroneState state);
}
