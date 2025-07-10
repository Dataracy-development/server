package com.dataracy.modules.reference.application.port.in.visitsource;

import com.dataracy.modules.reference.domain.model.VisitSource;

/**
 * 방문 경로 조회 유스케이스
 */
public interface FindVisitSourceUseCase {
    VisitSource findVisitSource(Long visitSourceId);
}
