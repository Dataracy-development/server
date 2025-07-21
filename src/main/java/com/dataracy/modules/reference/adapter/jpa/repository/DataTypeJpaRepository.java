package com.dataracy.modules.reference.adapter.jpa.repository;

import com.dataracy.modules.reference.adapter.jpa.entity.DataTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DataTypeJpaRepository extends JpaRepository<DataTypeEntity, Long> {
    /**
 * 주어진 ID에 해당하는 DataTypeEntity의 라벨을 Optional로 반환합니다.
 *
 * @param id 라벨을 조회할 DataTypeEntity의 ID
 * @return 해당 ID의 엔티티가 존재하면 라벨을 포함한 Optional, 없으면 빈 Optional
 */
    @Query("SELECT a.label FROM DataTypeEntity a WHERE a.id = :id")
Optional<String> findLabelById(Long id);
}
