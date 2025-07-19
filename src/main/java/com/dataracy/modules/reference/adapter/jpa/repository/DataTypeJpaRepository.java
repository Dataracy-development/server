package com.dataracy.modules.reference.adapter.jpa.repository;

import com.dataracy.modules.reference.adapter.jpa.entity.DataTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DataTypeJpaRepository extends JpaRepository<DataTypeEntity, Long> {
    Optional<String> findLabelById(Long id);
}
