package com.dataracy.modules.reference.application.port.out;

import com.dataracy.modules.reference.domain.model.VisitSource;

import java.util.List;
import java.util.Optional;

/**
 * VisitSource db에 접근하는 포트
 */
public interface VisitSourceRepositoryPort {
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
 * 지정된 ID를 가진 VisitSource 엔티티가 데이터베이스에 존재하는지 여부를 반환합니다.
 *
 * @param visitSourceId 존재 여부를 확인할 VisitSource의 고유 식별자
 * @return VisitSource가 존재하면 true, 존재하지 않으면 false
 */
boolean existsVisitSourceById(Long visitSourceId);
}
