package com.tinashe.dronescore.repository;

import com.tinashe.dronescore.common.jpa.BaseDao;
import com.tinashe.dronescore.model.Medication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicationRepository extends BaseDao<Medication> {
    Optional<Medication> findByCode(String code);
    List<Medication> findByCodeIn(List<String> codes);
    Page<Medication> findByCodeIn(Collection<String> codes, Pageable pageable);
}
