package com.dataracy.modules.user.adapter.persistence.repository.reference;

import com.dataracy.modules.user.adapter.persistence.entity.reference.OccupationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OccupationJpaRepository extends JpaRepository<OccupationEntity, Long> {
}
