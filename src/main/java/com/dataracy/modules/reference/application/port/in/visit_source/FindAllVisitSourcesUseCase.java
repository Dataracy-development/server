package com.dataracy.modules.reference.application.port.in.visit_source;

import com.dataracy.modules.reference.application.dto.response.allview.AllVisitSourcesResponse;

/**
 * 전체 VisitSource 조회 유스케이스
 */
public interface FindAllVisitSourcesUseCase {
    /**
     * 모든 방문 소스 정보를 조회합니다.
     *
     * @return 전체 방문 소스 목록이 포함된 응답 객체
     */
    AllVisitSourcesResponse allVisitSources();
}
