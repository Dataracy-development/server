package com.dataracy.modules.reference.application.port.in.visit_source;

import com.dataracy.modules.reference.application.dto.response.AllVisitSourcesResponse;

/**
 * 전체 VisitSource 조회 유스케이스
 */
public interface FindAllVisitSourcesUseCase {
    AllVisitSourcesResponse allVisitSources();
}
