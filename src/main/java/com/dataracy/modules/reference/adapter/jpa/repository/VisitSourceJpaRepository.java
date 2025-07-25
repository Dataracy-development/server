package com.dataracy.modules.reference.adapter.jpa.repository;

import com.dataracy.modules.reference.adapter.jpa.entity.VisitSourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface VisitSourceJpaRepository extends JpaRepository<VisitSourceEntity, Long> {
    /**
 * 주어진 ID에 해당하는 VisitSourceEntity의 라벨을 Optional로 반환합니다.
 *
 * @param id 조회할 VisitSourceEntity의 ID
 * @return 해당 ID의 라벨이 존재하면 Optional로 반환하며, 없으면 빈 Optional을 반환합니다.
 */
    @Query("SELECT a.label FROM VisitSourceEntity a WHERE a.id = :id")
Optional<String> findLabelById(Long id);
}
