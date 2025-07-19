package com.dataracy.modules.reference.adapter.jpa.repository;

import com.dataracy.modules.reference.adapter.jpa.entity.AnalysisPurposeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnalysisPurposeJpaRepository extends JpaRepository<AnalysisPurposeEntity, Long> {
    Optional<String> findLabelById(Long id);
}
