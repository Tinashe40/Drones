package com.tinashe.dronescore.repository;

import com.tinashe.dronescore.common.jpa.BaseDao;
import com.tinashe.dronescore.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends BaseDao<AuditLog> {
    Page<AuditLog> findByDroneSerialNumber(String droneSerialNumber, Pageable pageable);
}