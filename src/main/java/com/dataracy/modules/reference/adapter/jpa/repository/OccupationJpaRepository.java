package com.dataracy.modules.reference.adapter.jpa.repository;

import com.dataracy.modules.reference.adapter.jpa.entity.OccupationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OccupationJpaRepository extends JpaRepository<OccupationEntity, Long> {
}
