package com.dataracy.modules.reference.application.port.in.visit_source;

import com.dataracy.modules.reference.application.dto.response.AllVisitSourcesResponse;
import com.dataracy.modules.reference.domain.model.VisitSource;

/**
 * 방문 경로 조회 유스케이스
 */
public interface FindVisitSourceUseCase {
    /**
 * 지정된 방문 출처 ID에 해당하는 방문 출처 정보를 조회합니다.
 *
 * @param visitSourceId 조회할 방문 출처의 고유 식별자
 * @return 방문 출처 정보 응답 객체
 */
AllVisitSourcesResponse.VisitSourceResponse findVisitSource(Long visitSourceId);
}
