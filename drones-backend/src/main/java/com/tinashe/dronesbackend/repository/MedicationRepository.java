package com.tinashe.dronesbackend.repository;

import com.tinashe.dronesbackend.common.BaseDao;
import com.tinashe.dronesbackend.model.Medication;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicationRepository extends BaseDao<Medication> {
    Optional<Medication> findByCode(String code);
    List<Medication> findByCodeIn(List<String> codes);
}
