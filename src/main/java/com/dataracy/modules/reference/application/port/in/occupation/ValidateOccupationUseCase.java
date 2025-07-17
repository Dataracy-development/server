package com.dataracy.modules.reference.application.port.in.occupation;

/**
 * 직업 유효성 유스케이스
 */
public interface ValidateOccupationUseCase {
    /****
 * 주어진 직업 ID에 해당하는 직업 정보를 검증합니다.
 *
 * @param occupationId 검증할 직업의 고유 식별자
 */
void validateOccupation(Long occupationId);
}
