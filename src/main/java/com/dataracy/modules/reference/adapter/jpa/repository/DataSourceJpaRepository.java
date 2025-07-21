package com.dataracy.modules.reference.adapter.jpa.repository;

import com.dataracy.modules.reference.adapter.jpa.entity.DataSourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DataSourceJpaRepository extends JpaRepository<DataSourceEntity, Long> {
    /**
 * 지정된 ID에 해당하는 DataSourceEntity의 라벨을 Optional로 반환합니다.
 *
 * @param id 라벨을 조회할 DataSourceEntity의 ID
 * @return 해당 ID의 라벨이 존재하면 Optional로 감싸서 반환하며, 없으면 빈 Optional을 반환합니다.
 */
    @Query("SELECT a.label FROM DataSourceEntity a WHERE a.id = :id")
Optional<String> findLabelById(Long id);
}
