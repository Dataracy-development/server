package com.dataracy.modules.reference.application.port.in.authorlevel;

import com.dataracy.modules.reference.application.dto.response.AllAuthorLevelsResponse;

/**
 * 전체 AuthorLevel 조회 유스케이스
 */
public interface FindAllAuthorLevelsUseCase {
    AllAuthorLevelsResponse allAuthorLevels();
}
