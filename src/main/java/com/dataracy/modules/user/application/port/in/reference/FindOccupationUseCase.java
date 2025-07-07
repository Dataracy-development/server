package com.dataracy.modules.user.application.port.in.reference;

import com.dataracy.modules.user.domain.model.reference.Occupation;

/**
 * 직업 조회 유스케이스
 */
public interface FindOccupationUseCase {
    Occupation findOccupation(Long occupationId);
}
