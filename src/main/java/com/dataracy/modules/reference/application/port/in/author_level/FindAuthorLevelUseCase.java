package com.dataracy.modules.reference.application.port.in.author_level;

import com.dataracy.modules.reference.domain.model.AuthorLevel;

/**
 * 작성자 유형 조회 유스케이스
 */
public interface FindAuthorLevelUseCase {
    AuthorLevel findAuthorLevel(Long authorLevelId);
}
