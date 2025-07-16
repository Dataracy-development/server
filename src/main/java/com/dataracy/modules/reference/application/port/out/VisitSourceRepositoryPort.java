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
 * 주어진 ID에 해당하는 VisitSource 엔티티를 조회합니다.
 *
 * @param visitSourceId 조회할 VisitSource의 고유 식별자
 * @return 해당 ID에 해당하는 VisitSource가 존재하면 Optional로 반환하며, 없으면 빈 Optional을 반환합니다.
 */
    Optional<VisitSource> findVisitSourceById(Long visitSourceId);

    Boolean existsVisitSourceById(Long visitSourceId);
}
