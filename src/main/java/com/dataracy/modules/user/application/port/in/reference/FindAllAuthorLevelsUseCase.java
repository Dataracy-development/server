package com.dataracy.modules.user.application.port.in.reference;

import com.dataracy.modules.user.application.dto.response.reference.AllAuthorLevelsResponse;

/**
 * 전체 AuthorLevel 조회 유스케이스
 */
public interface FindAllAuthorLevelsUseCase {
    AllAuthorLevelsResponse allAuthorLevels();
}
