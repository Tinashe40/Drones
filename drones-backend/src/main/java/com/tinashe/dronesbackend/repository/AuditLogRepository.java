package com.tinashe.dronesbackend.repository;

import com.tinashe.dronesbackend.common.jpa.BaseDao;
import com.tinashe.dronesbackend.model.AuditLog;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends BaseDao<AuditLog> {
}