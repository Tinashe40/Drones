package com.tinashe.dronescore.service.Impl;

import com.tinashe.dronescore.dto.DroneDto;
import com.tinashe.dronescore.dto.LoadMedicationRequest;
import com.tinashe.dronescore.dto.MedicationDto;
import com.tinashe.dronescore.enums.DroneModel;
import com.tinashe.dronescore.enums.DroneState;
import com.tinashe.dronescore.exception.DroneException;
import com.tinashe.dronescore.exception.DroneNotFoundException;
import com.tinashe.dronescore.exception.LowBatteryException;
import com.tinashe.dronescore.exception.WeightLimitExceededException;
import com.tinashe.dronescore.model.AuditLog;
import com.tinashe.dronescore.model.Drone;
import com.tinashe.dronescore.model.Medication;
import com.tinashe.dronescore.repository.AuditLogRepository;
import com.tinashe.dronescore.repository.DroneRepository;
import com.tinashe.dronescore.repository.MedicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
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

    @Mock
    private AuditLogRepository auditLogRepository; // Add this line

    @InjectMocks
    private DroneServiceImpl droneService;

    private Drone drone;
    private DroneDto droneDto;
    private Medication medication1;
    private Medication medication2;
    private MedicationDto medicationDto1;
    private MedicationDto medicationDto2;

    private Drone drone2;
    private DroneDto droneDto2;


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


        drone2 = new Drone();
        drone2.setId(2L);
        drone2.setSerialNumber("DRN002");
        drone2.setModel(DroneModel.MIDDLEWEIGHT);
        drone2.setWeightLimit(200);
        drone2.setBatteryCapacity(90);
        drone2.setState(DroneState.LOADING);
        drone2.setMedications(new java.util.ArrayList<>());

        droneDto2 = new DroneDto();
        droneDto2.setSerialNumber("DRN002");
        droneDto2.setModel(DroneModel.MIDDLEWEIGHT);
        droneDto2.setWeightLimit(200);
        droneDto2.setBatteryCapacity(90);
        droneDto2.setState(DroneState.LOADING);

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

    @Test
    void getDroneBySerialNumber_success() {
        when(droneRepository.findBySerialNumber("DRN001")).thenReturn(Optional.of(drone));
        when(modelMapper.map(drone, DroneDto.class)).thenReturn(droneDto);

        DroneDto result = droneService.getDroneBySerialNumber("DRN001");

        assertNotNull(result);
        assertEquals("DRN001", result.getSerialNumber());
        verify(droneRepository, times(1)).findBySerialNumber("DRN001");
        verify(modelMapper, times(1)).map(drone, DroneDto.class);
    }

    @Test
    void getDroneBySerialNumber_droneNotFound() {
        when(droneRepository.findBySerialNumber("DRN001")).thenReturn(Optional.empty());

        assertThrows(DroneNotFoundException.class, () -> droneService.getDroneBySerialNumber("DRN001"));
        verify(droneRepository, times(1)).findBySerialNumber("DRN001");
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void updateDrone_success() {
        Drone updatedDrone = new Drone();
        updatedDrone.setId(1L);
        updatedDrone.setSerialNumber("DRN001");
        updatedDrone.setModel(DroneModel.LIGHTWEIGHT);
        updatedDrone.setWeightLimit(100);
        updatedDrone.setBatteryCapacity(80); // Updated battery
        updatedDrone.setState(DroneState.DELIVERING); // Updated state

        DroneDto updatedDroneDto = new DroneDto();
        updatedDroneDto.setSerialNumber("DRN001");
        updatedDroneDto.setModel(DroneModel.LIGHTWEIGHT);
        updatedDroneDto.setWeightLimit(100);
        updatedDroneDto.setBatteryCapacity(80);
        updatedDroneDto.setState(DroneState.DELIVERING);

        when(droneRepository.findBySerialNumber("DRN001")).thenReturn(Optional.of(drone));
        when(droneRepository.save(any(Drone.class))).thenReturn(updatedDrone);
        when(modelMapper.map(any(Drone.class), eq(DroneDto.class))).thenReturn(updatedDroneDto);

        DroneDto result = droneService.updateDrone("DRN001", updatedDroneDto);

        assertNotNull(result);
        assertEquals(80, result.getBatteryCapacity());
        assertEquals(DroneState.DELIVERING, result.getState());
        verify(droneRepository, times(1)).findBySerialNumber("DRN001");
        verify(droneRepository, times(1)).save(drone);
        verify(modelMapper, times(1)).map(updatedDrone, DroneDto.class);
    }

    @Test
    void updateDrone_droneNotFound() {
        DroneDto updatedDroneDto = new DroneDto();
        updatedDroneDto.setBatteryCapacity(80);
        updatedDroneDto.setState(DroneState.DELIVERING);

        when(droneRepository.findBySerialNumber("DRN001")).thenReturn(Optional.empty());

        assertThrows(DroneNotFoundException.class, () -> droneService.updateDrone("DRN001", updatedDroneDto));
        verify(droneRepository, times(1)).findBySerialNumber("DRN001");
        verify(droneRepository, never()).save(any(Drone.class));
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void deleteDrone_success() {
        when(droneRepository.findBySerialNumber("DRN001")).thenReturn(Optional.of(drone));
        doNothing().when(droneRepository).delete(any(Drone.class));

        droneService.deleteDrone("DRN001");

        verify(droneRepository, times(1)).findBySerialNumber("DRN001");
        verify(droneRepository, times(1)).delete(drone);
    }

    @Test
    void deleteDrone_droneNotFound() {
        when(droneRepository.findBySerialNumber("DRN001")).thenReturn(Optional.empty());

        assertThrows(DroneNotFoundException.class, () -> droneService.deleteDrone("DRN001"));
        verify(droneRepository, times(1)).findBySerialNumber("DRN001");
        verify(droneRepository, never()).delete(any(Drone.class));
    }

    @Test
    void getDroneAuditLogs_success() {
        AuditLog auditLog1 = new AuditLog("DRN001", 90, "STATE_CHANGED_TO_LOADING");
        AuditLog auditLog2 = new AuditLog("DRN001", 85, "MEDICATION_LOADED");
        List<AuditLog> auditLogs = Arrays.asList(auditLog1, auditLog2);

        com.tinashe.dronescore.dto.AuditLogDto auditLogDto1 = new com.tinashe.dronescore.dto.AuditLogDto();
        auditLogDto1.setDroneSerialNumber("DRN001");
        auditLogDto1.setBatteryCapacity(90);
        auditLogDto1.setEventType("STATE_CHANGED_TO_LOADING");

        com.tinashe.dronescore.dto.AuditLogDto auditLogDto2 = new com.tinashe.dronescore.dto.AuditLogDto();
        auditLogDto2.setDroneSerialNumber("DRN001");
        auditLogDto2.setBatteryCapacity(85);
        auditLogDto2.setEventType("MEDICATION_LOADED");

        when(droneRepository.findBySerialNumber("DRN001")).thenReturn(Optional.of(drone));
        when(auditLogRepository.findByDroneSerialNumber("DRN001")).thenReturn(auditLogs);
        when(modelMapper.map(auditLog1, com.tinashe.dronescore.dto.AuditLogDto.class)).thenReturn(auditLogDto1);
        when(modelMapper.map(auditLog2, com.tinashe.dronescore.dto.AuditLogDto.class)).thenReturn(auditLogDto2);

        List<com.tinashe.dronescore.dto.AuditLogDto> result = droneService.getDroneAuditLogs("DRN001");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("STATE_CHANGED_TO_LOADING", result.get(0).getEventType());
        assertEquals("MEDICATION_LOADED", result.get(1).getEventType());
        verify(droneRepository, times(1)).findBySerialNumber("DRN001");
        verify(auditLogRepository, times(1)).findByDroneSerialNumber("DRN001");
        verify(modelMapper, times(2)).map(any(AuditLog.class), eq(com.tinashe.dronescore.dto.AuditLogDto.class));
    }

    @Test
    void getDroneAuditLogs_droneNotFound() {
        when(droneRepository.findBySerialNumber("DRN001")).thenReturn(Optional.empty());

        assertThrows(DroneNotFoundException.class, () -> droneService.getDroneAuditLogs("DRN001"));
        verify(droneRepository, times(1)).findBySerialNumber("DRN001");
        verify(auditLogRepository, never()).findByDroneSerialNumber(anyString());
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void getAllDrones_noStateFilter() {
        List<Drone> allDrones = Arrays.asList(drone, drone2);
        when(droneRepository.findAll()).thenReturn(allDrones);
        when(modelMapper.map(drone, DroneDto.class)).thenReturn(droneDto);
        when(modelMapper.map(drone2, DroneDto.class)).thenReturn(droneDto2);

        List<DroneDto> result = droneService.getAllDrones(null);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("DRN001", result.get(0).getSerialNumber());
        assertEquals("DRN002", result.get(1).getSerialNumber());
        verify(droneRepository, times(1)).findAll();
        verify(droneRepository, never()).findAllByState(any(DroneState.class));
        verify(modelMapper, times(2)).map(any(Drone.class), eq(DroneDto.class));
    }

    @Test
    void getAllDrones_withStateFilter() {
        List<Drone> idleDrones = Collections.singletonList(drone);
        when(droneRepository.findAllByState(DroneState.IDLE)).thenReturn(idleDrones);
        when(modelMapper.map(drone, DroneDto.class)).thenReturn(droneDto);

        List<DroneDto> result = droneService.getAllDrones(DroneState.IDLE);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("DRN001", result.get(0).getSerialNumber());
        assertEquals(DroneState.IDLE, result.get(0).getState());
        verify(droneRepository, times(1)).findAllByState(DroneState.IDLE);
        verify(droneRepository, never()).findAll();
        verify(modelMapper, times(1)).map(any(Drone.class), eq(DroneDto.class));
    }
}
