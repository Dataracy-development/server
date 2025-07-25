package com.dataracy.modules.dataset.adapter.jpa.repository;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DataJpaRepository extends JpaRepository<DataEntity, Long> {
    @Query(value = "SELECT * FROM data WHERE data_id = :dataId", nativeQuery = true) // @Where 무시됨
    Optional<DataEntity> findIncludingDeletedData(@Param("dataId") Long dataId);
}
