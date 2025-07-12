package com.dataracy.modules.reference.application.port.in.author_level;

import com.dataracy.modules.reference.application.dto.response.allview.AllAuthorLevelsResponse;

/**
 * 작성자 유형 조회 유스케이스
 */
public interface FindAuthorLevelUseCase {
    /**
     * 주어진 저자 등급 ID에 해당하는 저자 등급 정보를 조회합니다.
     *
     * @param authorLevelId 조회할 저자 등급의 ID
     * @return 해당 저자 등급의 상세 정보
     */
    AllAuthorLevelsResponse.AuthorLevelResponse findAuthorLevel(Long authorLevelId);
}
