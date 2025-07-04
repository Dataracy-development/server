package com.dataracy.modules.user.application.port.in.reference;

import com.dataracy.modules.user.application.dto.response.reference.AllVisitSourcesResponse;

/**
 * 전체 VisitSource 조회 유스케이스
 */
public interface FindAllVisitSourcesUseCase {
    AllVisitSourcesResponse allVisitSources();
}
