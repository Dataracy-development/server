package com.dataracy.modules.dataset.adapter.jpa.repository;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DataMetadataJpaRepository extends JpaRepository<DataMetadataEntity, Long> {
    Optional<DataMetadataEntity> findByDataId(Long dataId);
}
