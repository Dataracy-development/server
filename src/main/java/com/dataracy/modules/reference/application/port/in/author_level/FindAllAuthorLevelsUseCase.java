package com.dataracy.modules.reference.application.port.in.author_level;

import com.dataracy.modules.reference.application.dto.response.allview.AllAuthorLevelsResponse;

/**
 * 전체 AuthorLevel 조회 유스케이스
 */
public interface FindAllAuthorLevelsUseCase {
    /**
 * 모든 저자 레벨 정보를 반환합니다.
 *
 * @return 전체 저자 레벨 정보를 포함하는 응답 객체
 */
    AllAuthorLevelsResponse allAuthorLevels();
}
