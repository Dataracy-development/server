package com.dataracy.modules.reference.adapter.persistence.repository.jpa;

import com.dataracy.modules.reference.adapter.persistence.entity.DataTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataTypeJpaRepository extends JpaRepository<DataTypeEntity, Long> {
}
