package com.dataracy.modules.reference.application.port.out;

import com.dataracy.modules.reference.domain.model.VisitSource;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * VisitSource db에 접근하는 포트
 */
public interface VisitSourcePort {
    /**
 * 데이터베이스에 저장된 모든 방문 경로(VisitSource) 엔티티의 목록을 반환합니다.
 *
 * @return 모든 VisitSource 엔티티의 리스트
 */
    List<VisitSource> findAllVisitSources();

    /**
 * 주어진 ID로 VisitSource 엔티티를 조회합니다.
 *
 * @param visitSourceId 조회할 VisitSource의 고유 식별자
 * @return VisitSource가 존재하면 해당 엔티티를 포함한 Optional, 존재하지 않으면 빈 Optional
 */
    Optional<VisitSource> findVisitSourceById(Long visitSourceId);

    /**
 * 주어진 ID의 VisitSource 엔티티가 데이터베이스에 존재하는지 확인합니다.
 *
 * @param visitSourceId 존재 여부를 확인할 VisitSource의 고유 ID
 * @return VisitSource가 존재하면 true, 존재하지 않으면 false
 */
boolean existsVisitSourceById(Long visitSourceId);

    /**
 * 지정된 방문 소스 ID에 해당하는 라벨을 반환합니다.
 *
 * @param visitSourceId 라벨을 조회할 방문 소스의 고유 ID
 * @return 라벨이 존재하면 해당 값을 포함한 Optional, 존재하지 않으면 빈 Optional
 */
Optional<String> getLabelById(Long visitSourceId);

    /**
 * 여러 방문 소스 ID에 대해 각 ID에 해당하는 라벨을 조회하여 맵으로 반환합니다.
 *
 * @param visitSourceIds 라벨을 조회할 방문 소스 ID 목록
 * @return 각 방문 소스 ID와 해당 라벨이 매핑된 맵
 */
Map<Long, String> getLabelsByIds(List<Long> visitSourceIds);
}
