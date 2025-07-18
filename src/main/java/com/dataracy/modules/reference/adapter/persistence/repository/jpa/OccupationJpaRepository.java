package com.dataracy.modules.reference.adapter.persistence.repository.jpa;

import com.dataracy.modules.reference.adapter.persistence.entity.OccupationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OccupationJpaRepository extends JpaRepository<OccupationEntity, Long> {
}
