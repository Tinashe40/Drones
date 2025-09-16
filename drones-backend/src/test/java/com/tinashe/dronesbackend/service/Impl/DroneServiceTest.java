package com.tinashe.dronesbackend.service.Impl;

import com.tinashe.dronesbackend.dto.DroneDto;
import com.tinashe.dronesbackend.dto.LoadMedicationRequest;
import com.tinashe.dronesbackend.dto.MedicationDto;
import com.tinashe.dronesbackend.enums.DroneModel;
import com.tinashe.dronesbackend.enums.DroneState;
import com.tinashe.dronesbackend.exception.DroneException;
import com.tinashe.dronesbackend.exception.DroneNotFoundException;
import com.tinashe.dronesbackend.exception.LowBatteryException;
import com.tinashe.dronesbackend.exception.WeightLimitExceededException;
import com.tinashe.dronesbackend.model.Drone;
import com.tinashe.dronesbackend.model.Medication;
import com.tinashe.dronesbackend.repository.DroneRepository;
import com.tinashe.dronesbackend.repository.MedicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DroneServiceTest {

    @Mock
    private DroneRepository droneRepository;

    @Mock
    private MedicationRepository medicationRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private DroneServiceImpl droneService;

    private Drone drone;
    private DroneDto droneDto;
    private Medication medication1;
    private Medication medication2;
    private MedicationDto medicationDto1;
    private MedicationDto medicationDto2;

    @BeforeEach
    void setUp() {
        drone = new Drone();
        drone.setId(1L);
        drone.setSerialNumber("DRN001");
        drone.setModel(DroneModel.LIGHTWEIGHT);
        drone.setWeightLimit(100);
        drone.setBatteryCapacity(100);
        drone.setState(DroneState.IDLE);
        drone.setMedications(new java.util.ArrayList<>());

        droneDto = new DroneDto();
        droneDto.setSerialNumber("DRN001");
        droneDto.setModel(DroneModel.LIGHTWEIGHT);
        droneDto.setWeightLimit(100);
        droneDto.setBatteryCapacity(100);
        droneDto.setState(DroneState.IDLE);

        medication1 = new Medication();
        medication1.setId(1L);
        medication1.setName("MedicationA");
        medication1.setWeight(50);
        medication1.setCode("MED_A");
        medication1.setImageUrl("urlA");

        medicationDto1 = new MedicationDto();
        medicationDto1.setName("MedicationA");
        medicationDto1.setWeight(50);
        medicationDto1.setCode("MED_A");
        medicationDto1.setImageUrl("urlA");

        medication2 = new Medication();
        medication2.setId(2L);
        medication2.setName("MedicationB");
        medication2.setWeight(30);
        medication2.setCode("MED_B");
        medication2.setImageUrl("urlB");

        medicationDto2 = new MedicationDto();
        medicationDto2.setName("MedicationB");
        medicationDto2.setWeight(30);
        medicationDto2.setCode("MED_B");
        medicationDto2.setImageUrl("urlB");
    }

    @Test
    void registerDrone_success() {
        when(modelMapper.map(any(DroneDto.class), eq(Drone.class))).thenReturn(drone);
        when(droneRepository.save(any(Drone.class))).thenReturn(drone);
        when(modelMapper.map(any(Drone.class), eq(DroneDto.class))).thenReturn(droneDto);

        DroneDto result = droneService.registerDrone(droneDto);

        assertNotNull(result);
        assertEquals("DRN001", result.getSerialNumber());
        verify(droneRepository, times(1)).save(any(Drone.class));
    }

    @Test
    void loadMedication_success() {
        LoadMedicationRequest request = new LoadMedicationRequest();
        request.setMedicationCodes(Arrays.asList("MED_A", "MED_B"));

        when(droneRepository.findBySerialNumber("DRN001")).thenReturn(Optional.of(drone));
        when(medicationRepository.findByCodeIn(Arrays.asList("MED_A", "MED_B"))).thenReturn(Arrays.asList(medication1, medication2));
        when(droneRepository.save(any(Drone.class))).thenReturn(drone);

        droneService.loadMedication("DRN001", request);

        assertEquals(DroneState.LOADING, drone.getState());
        assertEquals(2, drone.getMedications().size());
        verify(droneRepository, times(1)).save(drone);
    }

    @Test
    void loadMedication_droneNotFound() {
        LoadMedicationRequest request = new LoadMedicationRequest();
        request.setMedicationCodes(Collections.singletonList("MED_A"));

        when(droneRepository.findBySerialNumber("DRN001")).thenReturn(Optional.empty());

        assertThrows(DroneNotFoundException.class, () -> droneService.loadMedication("DRN001", request));
    }

    @Test
    void loadMedication_lowBattery() {
        drone.setBatteryCapacity(20);
        LoadMedicationRequest request = new LoadMedicationRequest();
        request.setMedicationCodes(Collections.singletonList("MED_A"));

        when(droneRepository.findBySerialNumber("DRN001")).thenReturn(Optional.of(drone));

        assertThrows(LowBatteryException.class, () -> droneService.loadMedication("DRN001", request));
    }

    @Test
    void loadMedication_invalidDroneState() {
        drone.setState(DroneState.DELIVERING);
        LoadMedicationRequest request = new LoadMedicationRequest();
        request.setMedicationCodes(Collections.singletonList("MED_A"));

        when(droneRepository.findBySerialNumber("DRN001")).thenReturn(Optional.of(drone));

        assertThrows(DroneException.class, () -> droneService.loadMedication("DRN001", request));
    }

    @Test
    void loadMedication_medicationNotFound() {
        LoadMedicationRequest request = new LoadMedicationRequest();
        request.setMedicationCodes(Arrays.asList("MED_A", "UNKNOWN_MED"));

        when(droneRepository.findBySerialNumber("DRN001")).thenReturn(Optional.of(drone));
        when(medicationRepository.findByCodeIn(Arrays.asList("MED_A", "UNKNOWN_MED"))).thenReturn(Collections.singletonList(medication1));

        assertThrows(DroneException.class, () -> droneService.loadMedication("DRN001", request));
    }

    @Test
    void loadMedication_weightLimitExceeded() {
        drone.setWeightLimit(70); // Drone can only carry 70
        drone.setMedications(Collections.singletonList(medication1)); // Already carrying 50
        LoadMedicationRequest request = new LoadMedicationRequest();
        request.setMedicationCodes(Collections.singletonList("MED_B")); // Trying to add 30

        when(droneRepository.findBySerialNumber("DRN001")).thenReturn(Optional.of(drone));
        when(medicationRepository.findByCodeIn(Collections.singletonList("MED_B"))).thenReturn(Collections.singletonList(medication2));

        assertThrows(WeightLimitExceededException.class, () -> droneService.loadMedication("DRN001", request));
    }

    @Test
    void getLoadedMedication_success() {
        drone.setMedications(Arrays.asList(medication1, medication2));
        when(droneRepository.findBySerialNumber("DRN001")).thenReturn(Optional.of(drone));
        when(modelMapper.map(medication1, MedicationDto.class)).thenReturn(medicationDto1);
        when(modelMapper.map(medication2, MedicationDto.class)).thenReturn(medicationDto2);

        List<MedicationDto> result = droneService.getLoadedMedication("DRN001");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("MED_A", result.get(0).getCode());
        assertEquals("MED_B", result.get(1).getCode());
    }

    @Test
    void getLoadedMedication_droneNotFound() {
        when(droneRepository.findBySerialNumber("DRN001")).thenReturn(Optional.empty());

        assertThrows(DroneNotFoundException.class, () -> droneService.getLoadedMedication("DRN001"));
    }

    @Test
    void getAvailableDrones_success() {
        Drone availableDrone = new Drone();
        availableDrone.setSerialNumber("DRN002");
        availableDrone.setState(DroneState.IDLE);
        availableDrone.setBatteryCapacity(80);

        DroneDto availableDroneDto = new DroneDto();
        availableDroneDto.setSerialNumber("DRN002");
        availableDroneDto.setState(DroneState.IDLE);
        availableDroneDto.setBatteryCapacity(80);

        when(droneRepository.findByStateAndBatteryCapacityGreaterThanEqual(DroneState.IDLE, 25))
                .thenReturn(Collections.singletonList(availableDrone));
        when(modelMapper.map(availableDrone, DroneDto.class)).thenReturn(availableDroneDto);

        List<DroneDto> result = droneService.getAvailableDrones();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("DRN002", result.get(0).getSerialNumber());
    }

    @Test
    void getDroneBatteryLevel_success() {
        when(droneRepository.findBySerialNumber("DRN001")).thenReturn(Optional.of(drone));

        int batteryLevel = droneService.getDroneBatteryLevel("DRN001");

        assertEquals(100, batteryLevel);
    }

    @Test
    void getDroneBatteryLevel_droneNotFound() {
        when(droneRepository.findBySerialNumber("DRN001")).thenReturn(Optional.empty());

        assertThrows(DroneNotFoundException.class, () -> droneService.getDroneBatteryLevel("DRN001"));
    }
}
