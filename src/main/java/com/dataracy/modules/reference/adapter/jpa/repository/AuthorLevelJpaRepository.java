package com.dataracy.modules.reference.adapter.jpa.repository;

import com.dataracy.modules.reference.adapter.jpa.entity.AuthorLevelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorLevelJpaRepository extends JpaRepository<AuthorLevelEntity, Long> {
    /**
 * 지정된 ID에 해당하는 AuthorLevelEntity의 라벨을 Optional로 반환합니다.
 *
 * @param id 조회할 AuthorLevelEntity의 ID
 * @return 해당 ID의 라벨이 존재하면 Optional로 감싸서 반환하며, 없으면 빈 Optional을 반환합니다.
 */
Optional<String> findLabelById(Long id);
}
