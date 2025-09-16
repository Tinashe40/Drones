package com.tinashe.dronesbackend.repository;

import com.tinashe.dronesbackend.model.Drone;
import com.tinashe.dronesbackend.model.DroneState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DroneRepository extends JpaRepository<Drone, Long> {
    Optional<Drone> findBySerialNumber(String serialNumber);
    List<Drone> findByStateAndBatteryCapacityGreaterThanEqual(DroneState state, int batteryCapacity);
}
