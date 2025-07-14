package com.dataracy.modules.data.adapter.persistence.repository;

import com.dataracy.modules.data.adapter.persistence.entity.DataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataJpaRepository extends JpaRepository<DataEntity, Long> {
}
