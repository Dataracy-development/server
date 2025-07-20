package com.dataracy.modules.reference.adapter.jpa.repository;

import com.dataracy.modules.reference.adapter.jpa.entity.TopicEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TopicJpaRepository extends JpaRepository<TopicEntity, Long> {
    /**
 * 주어진 ID에 해당하는 TopicEntity의 라벨을 조회합니다.
 *
 * @param id 조회할 TopicEntity의 ID
 * @return 라벨이 존재하면 Optional로 감싸진 문자열, 없으면 빈 Optional
 */
Optional<String> findLabelById(Long id);
}
