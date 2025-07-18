package com.dataracy.modules.data.adapter.persistence.repository.jpa;

import com.dataracy.modules.data.adapter.persistence.entity.DataMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataMetadataJpaRepository extends JpaRepository<DataMetadataEntity, Long> {
}
