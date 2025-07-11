package com.dataracy.modules.reference.adapter.persistence.repository;

import com.dataracy.modules.reference.adapter.persistence.entity.DataSourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataSourceJpaRepository extends JpaRepository<DataSourceEntity, Long> {
}
