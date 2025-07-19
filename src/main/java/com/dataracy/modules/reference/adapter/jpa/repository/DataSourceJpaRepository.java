package com.dataracy.modules.reference.adapter.jpa.repository;

import com.dataracy.modules.reference.adapter.jpa.entity.DataSourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DataSourceJpaRepository extends JpaRepository<DataSourceEntity, Long> {
    Optional<String> findLabelById(Long id);
}
