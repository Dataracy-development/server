package com.dataracy.modules.reference.application.port.in.visitsource;

import com.dataracy.modules.reference.application.dto.response.singleview.VisitSourceResponse;

/**
 * 방문 경로 조회 유스케이스
 */
public interface FindVisitSourceUseCase {
    /**
 * 주어진 방문 출처 ID로 해당 방문 출처의 상세 정보를 반환합니다.
 *
 * @param visitSourceId 조회할 방문 출처의 고유 ID
 * @return 지정된 ID에 해당하는 방문 출처 정보
 */
    VisitSourceResponse findVisitSource(Long visitSourceId);
}
