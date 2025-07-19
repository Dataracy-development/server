package com.dataracy.modules.data.adapter.jpa.repository;

import com.dataracy.modules.data.adapter.jpa.entity.DataMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataMetadataJpaRepository extends JpaRepository<DataMetadataEntity, Long> {
}
