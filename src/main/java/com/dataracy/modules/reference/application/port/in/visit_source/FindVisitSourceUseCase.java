package com.dataracy.modules.reference.application.port.in.visit_source;

import com.dataracy.modules.reference.application.dto.response.AllVisitSourcesResponse;
import com.dataracy.modules.reference.domain.model.VisitSource;

/**
 * 방문 경로 조회 유스케이스
 */
public interface FindVisitSourceUseCase {
    AllVisitSourcesResponse.VisitSourceResponse findVisitSource(Long visitSourceId);
}
