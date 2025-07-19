package com.dataracy.modules.data.adapter.jpa.repository;

import com.dataracy.modules.data.adapter.jpa.entity.DataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataJpaRepository extends JpaRepository<DataEntity, Long> {
}
