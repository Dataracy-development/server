package com.dataracy.modules.reference.application.port.in.author_level;

import com.dataracy.modules.reference.application.dto.response.AllAuthorLevelsResponse;

/**
 * 작성자 유형 조회 유스케이스
 */
public interface FindAuthorLevelUseCase {
    AllAuthorLevelsResponse.AuthorLevelResponse findAuthorLevel(Long authorLevelId);
}
