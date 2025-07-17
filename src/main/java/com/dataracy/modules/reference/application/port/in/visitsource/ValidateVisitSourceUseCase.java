package com.dataracy.modules.reference.application.port.in.visitsource;

/**
 * 방문 경로 유효성 유스케이스
 */
public interface ValidateVisitSourceUseCase {
    /**
 * 지정된 방문 출처 ID에 대해 유효성을 검사합니다.
 *
 * @param visitSourceId 유효성 검사를 수행할 방문 출처의 ID
 */
void validateVisitSource(Long visitSourceId);
}
