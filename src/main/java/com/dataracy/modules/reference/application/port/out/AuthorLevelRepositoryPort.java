package com.dataracy.modules.reference.application.port.out;

import com.dataracy.modules.reference.domain.model.AuthorLevel;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * AuthorLevel db에 접근하는 포트
 */
public interface AuthorLevelRepositoryPort {
    /**
 * 데이터베이스에 저장된 모든 작성자 유형(AuthorLevel) 엔티티의 목록을 반환합니다.
 *
 * @return 저장된 모든 AuthorLevel 엔티티의 리스트
 */
    List<AuthorLevel> findAllAuthorLevels();

    /**
 * 주어진 ID에 해당하는 AuthorLevel 엔티티를 반환합니다.
 *
 * @param authorLevelId 조회할 AuthorLevel의 고유 식별자
 * @return 해당 ID의 AuthorLevel이 존재하면 Optional로 반환하며, 존재하지 않으면 빈 Optional을 반환합니다.
 */
    Optional<AuthorLevel> findAuthorLevelById(Long authorLevelId);

    /**
 * 지정된 ID를 가진 AuthorLevel 엔티티가 데이터베이스에 존재하는지 여부를 반환합니다.
 *
 * @param authorLevelId 확인할 AuthorLevel의 고유 식별자
 * @return 엔티티가 존재하면 true, 존재하지 않으면 false
 */
boolean existsAuthorLevelById(Long authorLevelId);

    /**
 * 주어진 ID에 해당하는 AuthorLevel 엔티티의 라벨을 반환합니다.
 *
 * @param authorLevelId 라벨을 조회할 AuthorLevel의 고유 ID
 * @return 라벨이 존재하면 해당 값을 포함한 Optional, 존재하지 않으면 빈 Optional
 */
Optional<String> getLabelById(Long authorLevelId);

    /**
 * 주어진 여러 AuthorLevel ID에 대해 각 ID에 해당하는 라벨을 매핑하여 반환합니다.
 *
 * @param authorLevelIds 라벨을 조회할 AuthorLevel ID 목록
 * @return 각 AuthorLevel ID와 해당 라벨이 매핑된 Map 객체
 */
Map<Long, String> getLabelsByIds(List<Long> authorLevelIds);
}
