package com.tinashe.dronescore.dto;

import lombok.Data;
import java.time.LocalDateTime; // Import for LocalDateTime
import org.springframework.hateoas.RepresentationModel; // Import for RepresentationModel

@Data
public class AuditLogDto extends RepresentationModel<AuditLogDto> { // Extend RepresentationModel
    private String id;
    private String droneSerialNumber;
    private int batteryCapacity;
    private String eventType;
    private String userId; // New field
    private LocalDateTime timestamp; // Replaced createdDate
    private String details; // New field
}
