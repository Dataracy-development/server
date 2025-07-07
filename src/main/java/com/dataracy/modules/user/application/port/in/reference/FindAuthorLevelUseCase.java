package com.dataracy.modules.user.application.port.in.reference;

import com.dataracy.modules.user.domain.model.reference.AuthorLevel;

/**
 * 작성자 유형 조회 유스케이스
 */
public interface FindAuthorLevelUseCase {
    AuthorLevel findAuthorLevel(Long authorLevelId);
}
