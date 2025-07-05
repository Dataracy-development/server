package com.dataracy.modules.user.application.port.in.reference;

import com.dataracy.modules.user.domain.model.reference.VisitSource;

/**
 * 방문 경로 조회 유스케이스
 */
public interface FindVisitSourceUseCase {
    VisitSource findVisitSource(Long visitSourceId);
}
