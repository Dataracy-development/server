package com.dataracy.modules.reference.adapter.jpa.repository;

import com.dataracy.modules.reference.adapter.jpa.entity.OccupationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OccupationJpaRepository extends JpaRepository<OccupationEntity, Long> {
    Optional<String> findLabelById(Long id);
}
