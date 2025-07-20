package com.dataracy.modules.reference.adapter.jpa.repository;

import com.dataracy.modules.reference.adapter.jpa.entity.DataTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DataTypeJpaRepository extends JpaRepository<DataTypeEntity, Long> {
    /**
 * 주어진 ID에 해당하는 DataTypeEntity의 라벨을 조회합니다.
 *
 * @param id 조회할 DataTypeEntity의 ID
 * @return 해당 ID의 라벨이 존재하면 Optional로 반환하며, 없으면 빈 Optional을 반환합니다.
 */
Optional<String> findLabelById(Long id);
}
