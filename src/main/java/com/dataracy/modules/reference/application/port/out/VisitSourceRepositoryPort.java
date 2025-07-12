package com.dataracy.modules.reference.application.port.out;

import com.dataracy.modules.reference.domain.model.VisitSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * VisitSource db에 접근하는 포트
 */
@Repository
public interface VisitSourceRepositoryPort {
    /**
     * 데이터베이스에 저장된 모든 방문 경로 목록을 반환합니다.
     *
     * @return 모든 방문 경로의 리스트
     */
    List<VisitSource> allVisitSources();

    /**
     * 주어진 ID에 해당하는 VisitSource 객체를 반환합니다.
     *
     * @param visitSourceId 조회할 방문 경로의 고유 식별자
     * @return 해당 ID의 VisitSource 객체
     */
    Optional<VisitSource> findVisitSourceById(Long visitSourceId);
}
