package com.dataracy.modules.reference.application.port.in.occupation;

import com.dataracy.modules.reference.domain.model.Occupation;

/**
 * 직업 조회 유스케이스
 */
public interface FindOccupationUseCase {
    Occupation findOccupation(Long occupationId);
}
