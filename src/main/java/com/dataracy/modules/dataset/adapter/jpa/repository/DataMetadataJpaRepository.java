package com.dataracy.modules.dataset.adapter.jpa.repository;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataMetadataJpaRepository extends JpaRepository<DataMetadataEntity, Long> {
}
